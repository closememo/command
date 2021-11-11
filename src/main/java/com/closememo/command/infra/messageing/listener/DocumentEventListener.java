package com.closememo.command.infra.messageing.listener;

import com.closememo.command.application.SystemCommandRequester;
import com.closememo.command.application.document.DeleteDocumentCommand;
import com.closememo.command.domain.account.AccountDeletedEvent;
import com.closememo.command.domain.document.Document;
import com.closememo.command.domain.document.DocumentRepository;
import com.closememo.command.infra.messageing.publisher.MessagePublisher;
import java.util.stream.Stream;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DocumentEventListener {

  private final DocumentRepository documentRepository;
  private final MessagePublisher messagePublisher;

  public DocumentEventListener(
      DocumentRepository documentRepository,
      MessagePublisher messagePublisher) {
    this.documentRepository = documentRepository;
    this.messagePublisher = messagePublisher;
  }

  @ServiceActivator(inputChannel = "AccountDeletedEvent")
  @Transactional
  public void handle(AccountDeletedEvent payload) {
    try (Stream<Document> documents = documentRepository.findAllByOwnerId(payload.getAccountId())) {
      documents.forEach(document -> {
        DeleteDocumentCommand command = new DeleteDocumentCommand(
            SystemCommandRequester.getInstance(), document.getId());
        messagePublisher.publish(command);
      });
    }
  }
}
