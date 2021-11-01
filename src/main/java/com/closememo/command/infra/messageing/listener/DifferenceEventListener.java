package com.closememo.command.infra.messageing.listener;

import com.closememo.command.domain.difference.DifferenceRepository;
import com.closememo.command.domain.difference.LineDelta;
import com.closememo.command.domain.document.DocumentDeletedEvent;
import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.domain.document.DocumentUpdatedEvent;
import com.closememo.command.infra.helper.DocumentDiffUtils;
import com.closememo.command.application.SystemCommandRequester;
import com.closememo.command.application.difference.CreateDifferenceCommand;
import com.closememo.command.application.difference.DeleteDifferenceCommand;
import com.closememo.command.infra.messageing.publisher.MessagePublisher;
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

  @ServiceActivator(inputChannel = "DocumentUpdatedEvent")
  public void handle(DocumentUpdatedEvent payload) {
    String previousContent = payload.getPreviousContent();
    String content = payload.getContent();

    List<LineDelta> lineDeltas = DocumentDiffUtils.getLineChanges(previousContent, content);

    CreateDifferenceCommand command = new CreateDifferenceCommand(
        SystemCommandRequester.getInstance(), new DocumentId(payload.getAggregateId()),
        payload.getPreviousVersion(), lineDeltas);

    messagePublisher.publish(command);
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
