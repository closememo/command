package com.closememo.command.infra.messageing.listener;

import static com.closememo.command.domain.difference.Difference.NUMBER_OF_DIFFERENCE_LIMIT;

import com.closememo.command.application.SystemCommandRequester;
import com.closememo.command.application.difference.CreateDifferenceCommand;
import com.closememo.command.application.difference.DeleteDifferenceCommand;
import com.closememo.command.domain.difference.Difference;
import com.closememo.command.domain.difference.DifferenceCreatedEvent;
import com.closememo.command.domain.difference.DifferenceRepository;
import com.closememo.command.domain.difference.LineDelta;
import com.closememo.command.domain.document.DocumentDeletedEvent;
import com.closememo.command.domain.document.DocumentDifferencesClearedEvent;
import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.domain.document.DocumentUpdatedEvent;
import com.closememo.command.infra.diff.DocumentDiffUtils;
import com.closememo.command.infra.messageing.publisher.MessagePublisher;
import java.util.Comparator;
import java.util.List;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

@Component
public class DifferenceEventListener {

  private final DifferenceRepository differenceRepository;
  private final MessagePublisher messagePublisher;

  public DifferenceEventListener(
      DifferenceRepository differenceRepository,
      MessagePublisher messagePublisher) {
    this.differenceRepository = differenceRepository;
    this.messagePublisher = messagePublisher;
  }

  @ServiceActivator(inputChannel = "DifferenceCreatedEvent")
  public void handle(DifferenceCreatedEvent payload) {
    DocumentId documentId = payload.getDocumentId();
    if (differenceRepository.countByDocumentId(documentId) <= NUMBER_OF_DIFFERENCE_LIMIT) {
      return;
    }

    differenceRepository.findAllByDocumentId(documentId).stream()
        .sorted(Comparator.comparing(Difference::getDocumentVersion).reversed())
        .skip(NUMBER_OF_DIFFERENCE_LIMIT)
        .forEach(difference -> {
          DeleteDifferenceCommand command = new DeleteDifferenceCommand(
              SystemCommandRequester.getInstance(), difference.getId());
          messagePublisher.publish(command);
        });
  }

  @ServiceActivator(inputChannel = "DocumentUpdatedEvent")
  public void handle(DocumentUpdatedEvent payload) {
    String previousContent = payload.getPreviousContent();
    String content = payload.getContent();

    List<LineDelta> lineDeltas = DocumentDiffUtils.getLineChanges(previousContent, content);

    CreateDifferenceCommand command = new CreateDifferenceCommand(
        SystemCommandRequester.getInstance(), payload.getOwnerId(), payload.getDocumentId(),
        payload.getPreviousVersion(), lineDeltas);

    messagePublisher.publish(command);
  }

  @ServiceActivator(inputChannel = "DocumentDifferencesClearedEvent")
  public void handle(DocumentDifferencesClearedEvent payload) {
    differenceRepository.findAllByDocumentId(payload.getDocumentId()).forEach(difference -> {
      DeleteDifferenceCommand command = new DeleteDifferenceCommand(
          SystemCommandRequester.getInstance(), difference.getId());
      messagePublisher.publish(command);
    });
  }

  @ServiceActivator(inputChannel = "DocumentDeletedEvent")
  public void handle(DocumentDeletedEvent payload) {
    differenceRepository.findAllByDocumentId(payload.getDocumentId()).forEach(difference -> {
      DeleteDifferenceCommand command = new DeleteDifferenceCommand(
          SystemCommandRequester.getInstance(), difference.getId());
      messagePublisher.publish(command);
    });
  }
}
