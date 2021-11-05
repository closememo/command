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
import com.closememo.command.infra.elasticsearch.ElasticsearchClient;
import com.closememo.command.infra.elasticsearch.request.DeletePostRequest;
import com.closememo.command.infra.elasticsearch.request.IndexPostRequest;
import com.closememo.command.infra.elasticsearch.request.UpdatePostRequest;
import com.closememo.command.infra.http.mail.MailClient;
import com.closememo.command.infra.http.mail.SendMailRequest;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentCommandHandler {

  private final AccountRepository accountRepository;
  private final DocumentRepository documentRepository;
  private final ElasticsearchClient elasticsearchClient;
  private final MailClient mailClient;

  public DocumentCommandHandler(
      AccountRepository accountRepository,
      DocumentRepository documentRepository,
      ElasticsearchClient elasticsearchClient,
      MailClient mailClient) {
    this.accountRepository = accountRepository;
    this.documentRepository = documentRepository;
    this.elasticsearchClient = elasticsearchClient;
    this.mailClient = mailClient;
  }

  @Transactional
  @ServiceActivator(inputChannel = "CreateDocumentCommand")
  public DocumentId handle(CreateDocumentCommand command) {
    Document document = Document.newOne(documentRepository, command.getOwnerId(),
        command.getTitle(), command.getContent(), command.getTags());

    Document savedDocument = documentRepository.save(document);

    IndexPostRequest request = new IndexPostRequest(savedDocument);
    elasticsearchClient.indexPost(request);

    return savedDocument.getId();
  }

  @Transactional
  @ServiceActivator(inputChannel = "UpdateDocumentCommand")
  public DocumentId handle(UpdateDocumentCommand command) {
    Document document = documentRepository.findById(command.getDocumentId())
        .orElseThrow(DocumentNotFoundException::new);
    checkAuthority(command, document.getOwnerId());

    document.update(command.getTitle(), command.getContent(), command.getTags());
    Document savedDocument = documentRepository.save(document);

    UpdatePostRequest request = new UpdatePostRequest(savedDocument);
    elasticsearchClient.updatePost(request);

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

    DeletePostRequest request = new DeletePostRequest(document);
    elasticsearchClient.deletePost(request);

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

          DeletePostRequest request = new DeletePostRequest(document);
          elasticsearchClient.deletePost(request);
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

        DeletePostRequest request = new DeletePostRequest(document);
        elasticsearchClient.deletePost(request);
      });
    }

    return Success.getInstance();
  }

  private static void checkAuthority(Command command, AccountId ownerId) {
    if (!command.equalsAccountRequester(ownerId)) {
      throw new AccessDeniedException();
    }
  }
}
