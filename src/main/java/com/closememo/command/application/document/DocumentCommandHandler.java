package com.closememo.command.application.document;

import com.closememo.command.application.Command;
import com.closememo.command.application.Success;
import com.closememo.command.domain.AccessDeniedException;
import com.closememo.command.domain.account.Account;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.account.AccountNotFoundException;
import com.closememo.command.domain.account.AccountRepository;
import com.closememo.command.domain.category.Category;
import com.closememo.command.domain.category.CategoryId;
import com.closememo.command.domain.category.CategoryNotFoundException;
import com.closememo.command.domain.category.CategoryRepository;
import com.closememo.command.domain.document.Document;
import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.domain.document.DocumentNotFoundException;
import com.closememo.command.domain.document.DocumentOption;
import com.closememo.command.domain.document.DocumentRepository;
import com.closememo.command.infra.http.mail.MailClient;
import com.closememo.command.infra.http.mail.SendMailRequest;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentCommandHandler {

  private static final Pattern LOCAL_DATE_PATTERN =
      Pattern.compile("^(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})$");

  private final AccountRepository accountRepository;
  private final CategoryRepository categoryRepository;
  private final DocumentRepository documentRepository;
  private final MailClient mailClient;

  public DocumentCommandHandler(
      AccountRepository accountRepository,
      CategoryRepository categoryRepository,
      DocumentRepository documentRepository,
      MailClient mailClient) {
    this.accountRepository = accountRepository;
    this.categoryRepository = categoryRepository;
    this.documentRepository = documentRepository;
    this.mailClient = mailClient;
  }

  @Transactional
  @ServiceActivator(inputChannel = "CreateDocumentCommand")
  public DocumentId handle(CreateDocumentCommand command) {
    DocumentOption option = new DocumentOption(command.getOption().getHasAutoTag());

    Document document = Document.newOne(documentRepository, command.getOwnerId(),
        command.getCategoryId(), command.getTitle(), command.getContent(), command.getTags(), option);

    Document savedDocument = documentRepository.save(document);
    return savedDocument.getId();
  }

  @Transactional
  @ServiceActivator(inputChannel = "CreateLocalDocumentsCommand")
  public List<DocumentId> handle(CreateLocalDocumentsCommand command) {

    // TODO: 이후 root category 가 없으면 예외 처리하도록 수정
    Category category = categoryRepository.findRootCategory()
        .orElse(null);
    CategoryId categoryId = category != null ? category.getId() : null;

    return command.getLocalDocuments().stream()
        .map(localDocument -> {
          ZonedDateTime createdAt = from(localDocument.getLocalFormedDateString());
          Document document = Document.newLocalOne(documentRepository, command.getOwnerId(),
              categoryId, localDocument.getTitle(), localDocument.getContent(), createdAt);

          Document savedDocument = documentRepository.save(document);
          return savedDocument.getId();
        })
        .collect(Collectors.toList());
  }

  private static ZonedDateTime from(String localFormedDateString) {

    Matcher matcher = LOCAL_DATE_PATTERN.matcher(localFormedDateString);

    if (!matcher.find()) {
      return ZonedDateTime.now();
    }

    int year = Integer.parseInt(matcher.group(1));
    int month = Integer.parseInt(matcher.group(2));
    int dayOfMonth = Integer.parseInt(matcher.group(3));
    int hour = Integer.parseInt(matcher.group(4));
    int minute = Integer.parseInt(matcher.group(5));
    int second = Integer.parseInt(matcher.group(6));

    return ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, 0,
        ZoneId.systemDefault());
  }

  @Transactional
  @ServiceActivator(inputChannel = "UpdateDocumentCommand")
  public DocumentId handle(UpdateDocumentCommand command) {
    DocumentOption option = new DocumentOption(command.getOption().getHasAutoTag());

    Document document = documentRepository.findById(command.getDocumentId())
        .orElseThrow(DocumentNotFoundException::new);
    checkAuthority(command, document.getOwnerId());

    document.update(command.getTitle(), command.getContent(), command.getTags(), option);
    Document savedDocument = documentRepository.save(document);

    return savedDocument.getId();
  }

  @Transactional
  @ServiceActivator(inputChannel = "DeleteDocumentCommand")
  public Success handle(DeleteDocumentCommand command) {
    Document document = documentRepository.findById(command.getDocumentId())
        .orElseThrow(DocumentNotFoundException::new);
    checkAuthority(command, document.getOwnerId());

    document.delete();
    documentRepository.delete(document);

    return Success.getInstance();
  }

  @Transactional
  @ServiceActivator(inputChannel = "DeleteDocumentsCommand")
  public Success handle(DeleteDocumentsCommand command) {
    documentRepository.findAllByIdIn(command.getDocumentIds())
        .peek(document -> checkAuthority(command, document.getOwnerId()))
        .forEach(document -> {
          document.delete();
          documentRepository.delete(document);
        });

    return Success.getInstance();
  }

  @Transactional
  @ServiceActivator(inputChannel = "MailDocumentsCommand")
  public Success handle(MailDocumentsCommand command) {
    Account account = accountRepository.findById(command.getAccountId())
        .orElseThrow(AccountNotFoundException::new);
    List<Document> documents = documentRepository.findAllByIdIn(command.getDocumentIds())
        .peek(document -> checkAuthority(command, document.getOwnerId()))
        .collect(Collectors.toList());

    SendMailRequest sendMailRequest = new SendMailRequest(account.getEmail(), documents);
    mailClient.sendMail(sendMailRequest);

    if (command.isNeedToDelete()) {
      documents.forEach(document -> {
        document.delete();
        documentRepository.delete(document);
      });
    }

    return Success.getInstance();
  }

  @Transactional
  @ServiceActivator(inputChannel = "UpdateAutoTagsCommand")
  public Success handle(UpdateAutoTagsCommand command) {
    if (!command.isSystemRequester()) {
      throw new AccessDeniedException();
    }

    Document document = documentRepository.findById(command.getDocumentId())
        .orElseThrow(DocumentNotFoundException::new);

    if (document.getOption().isHasAutoTag()) {
      document.updateAutoTags(command.getAutoTags());
    }

    return Success.getInstance();
  }

  private static void checkAuthority(Command command, AccountId ownerId) {
    if (command.isReliableRequester()) {
      return;
    }

    if (!command.equalsAccountRequester(ownerId)) {
      throw new AccessDeniedException();
    }
  }
}
