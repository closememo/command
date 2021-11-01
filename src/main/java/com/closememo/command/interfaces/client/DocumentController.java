package com.closememo.command.interfaces.client;

import com.closememo.command.interfaces.client.requests.document.DeleteDocumentRequest;
import com.closememo.command.interfaces.client.requests.document.DeleteDocumentsRequest;
import com.closememo.command.interfaces.client.requests.document.UpdateDocumentRequest;
import com.closememo.command.application.AccountCommandRequester;
import com.closememo.command.application.CommandGateway;
import com.closememo.command.application.document.CreateDocumentCommand;
import com.closememo.command.application.document.DeleteDocumentCommand;
import com.closememo.command.application.document.DeleteDocumentsCommand;
import com.closememo.command.application.document.UpdateDocumentCommand;
import com.closememo.command.config.openapi.apitags.DocumentApiTag;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.interfaces.client.requests.document.CreateDocumentRequest;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@DocumentApiTag
@ClientCommandInterface
public class DocumentController {

  private final CommandGateway commandGateway;

  public DocumentController(CommandGateway commandGateway) {
    this.commandGateway = commandGateway;
  }

  @Operation(summary = "Create Document")
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/create-document")
  public DocumentId createDocument(@RequestBody @Valid CreateDocumentRequest request,
      @AuthenticationPrincipal AccountId accountId) {

    AccountCommandRequester requester = new AccountCommandRequester(accountId);
    CreateDocumentCommand command = new CreateDocumentCommand(requester, accountId,
            request.getTitle(), request.getContent(), request.getTags());

    return commandGateway.request(command);
  }

  @Operation(summary = "Update Document")
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/update-document")
  public DocumentId updateDocument(@RequestBody @Valid UpdateDocumentRequest request,
      @AuthenticationPrincipal AccountId accountId) {

    AccountCommandRequester requester = new AccountCommandRequester(accountId);
    DocumentId documentId = new DocumentId(request.getDocumentId());
    UpdateDocumentCommand command = new UpdateDocumentCommand(requester, documentId,
        request.getTitle(), request.getContent(), request.getTags());

    return commandGateway.request(command);
  }

  @Operation(summary = "Delete Document")
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/delete-document")
  public void deleteDocument(@RequestBody @Valid DeleteDocumentRequest request,
      @AuthenticationPrincipal AccountId accountId) {

    AccountCommandRequester requester = new AccountCommandRequester(accountId);
    DocumentId documentId = new DocumentId(request.getDocumentId());
    DeleteDocumentCommand command = new DeleteDocumentCommand(requester, documentId);

    commandGateway.request(command);
  }

  @Operation(summary = "Delete Documents")
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/delete-documents")
  public void deleteDocument(@RequestBody @Valid DeleteDocumentsRequest request,
      @AuthenticationPrincipal AccountId accountId) {

    AccountCommandRequester requester = new AccountCommandRequester(accountId);
    List<DocumentId> documentIds = request.getDocumentIds().stream()
        .map(DocumentId::new)
        .collect(Collectors.toList());
    DeleteDocumentsCommand command = new DeleteDocumentsCommand(requester, documentIds);

    commandGateway.request(command);
  }
}
