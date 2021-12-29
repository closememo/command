package com.closememo.command.interfaces.system;

import com.closememo.command.application.CommandGateway;
import com.closememo.command.application.SystemCommandRequester;
import com.closememo.command.application.document.UpdateAutoTagsCommand;
import com.closememo.command.config.openapi.apitags.SystemApiTag;
import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.interfaces.system.requests.documents.UpdateAutoTagsRequest;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@SystemApiTag
@SystemCommandInterface
public class SystemDocumentController {

  private final CommandGateway commandGateway;

  public SystemDocumentController(CommandGateway commandGateway) {
    this.commandGateway = commandGateway;
  }

  @PostMapping("/update-auto-tags")
  public void updateAutoTags(@RequestBody @Valid UpdateAutoTagsRequest request) {

    DocumentId documentId = new DocumentId(request.getDocumentId());
    UpdateAutoTagsCommand command = new UpdateAutoTagsCommand(SystemCommandRequester.getInstance(),
        documentId, request.getAutoTags());

    commandGateway.request(command);
  }
}
