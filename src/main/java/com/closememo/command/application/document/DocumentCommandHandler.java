package com.closememo.command.application.document;

import com.closememo.command.application.Command;
import com.closememo.command.application.Success;
import com.closememo.command.domain.AccessDeniedException;
import com.closememo.command.domain.account.Account;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.account.AccountNotFoundException;
import com.closememo.command.domain.account.AccountRepository;
import com.closememo.command.domain.document.Document;
import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.domain.document.DocumentNotFoundException;
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
  private final DocumentRepository documentRepository;
  private final MailClient mailClient;

  public DocumentCommandHandler(
      AccountRepository accountRepository,
      DocumentRepository documentRepository,
      MailClient mailClient) {
    this.accountRepository = accountRepository;
    this.documentRepository = documentRepository;
    this.mailClient = mailClient;
  }

  @Transactional
  @ServiceActivator(inputChannel = "CreateDocumentCommand")
  public DocumentId handle(CreateDocumentCommand command) {
    Document document = Document.newOne(documentRepository, command.getOwnerId(),
        command.getTitle(), command.getContent(), command.getTags());

    Document savedDocument = documentRepository.save(document);
    return savedDocument.getId();
  }

  @Transactional
  @ServiceActivator(inputChannel = "CreateLocalDocumentsCommand")
  public List<DocumentId> handle(CreateLocalDocumentsCommand command) {

    return command.getLocalDocuments().stream()
        .map(localDocument -> {
          ZonedDateTime createdAt = from(localDocument.getLocalFormedDateString());
          Document document = Document.newLocalOne(documentRepository, command.getOwnerId(),
              localDocument.getTitle(), localDocument.getContent(), createdAt);

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
    Document document = documentRepository.findById(command.getDocumentId())
        .orElseThrow(DocumentNotFoundException::new);
    checkAuthority(command, document.getOwnerId());

    document.update(command.getTitle(), command.getContent(), command.getTags());
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
    document.updateAutoTags(command.getAutoTags());

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
