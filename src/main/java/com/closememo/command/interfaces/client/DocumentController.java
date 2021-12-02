package com.closememo.command.interfaces.client;

import com.closememo.command.application.AccountCommandRequester;
import com.closememo.command.application.CommandGateway;
import com.closememo.command.application.document.CreateDocumentCommand;
import com.closememo.command.application.document.CreateLocalDocumentsCommand;
import com.closememo.command.application.document.DeleteDocumentCommand;
import com.closememo.command.application.document.DeleteDocumentsCommand;
import com.closememo.command.application.document.MailDocumentsCommand;
import com.closememo.command.application.document.UpdateDocumentCommand;
import com.closememo.command.config.openapi.apitags.DocumentApiTag;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.infra.projection.WaitForProjection;
import com.closememo.command.interfaces.client.requests.document.CreateDocumentRequest;
import com.closememo.command.interfaces.client.requests.document.CreateLocalDocumentsRequest;
import com.closememo.command.interfaces.client.requests.document.DeleteDocumentRequest;
import com.closememo.command.interfaces.client.requests.document.DeleteDocumentsRequest;
import com.closememo.command.interfaces.client.requests.document.MailDocumentsRequest;
import com.closememo.command.interfaces.client.requests.document.UpdateDocumentRequest;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
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

  @WaitForProjection
  @Operation(summary = "Create Document")
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/create-document")
  public DocumentId createDocument(@RequestBody @Valid CreateDocumentRequest request,
      @AuthenticationPrincipal AccountId accountId) {

    String title = Optional.ofNullable(request.getTitle()).orElse(StringUtils.EMPTY);
    List<String> tags = Optional.ofNullable(request.getTags()).orElse(Collections.emptyList());

    AccountCommandRequester requester = new AccountCommandRequester(accountId);
    CreateDocumentCommand command = new CreateDocumentCommand(requester, accountId,
        title, request.getContent(), tags);

    return commandGateway.request(command);
  }

  @WaitForProjection
  @Operation(summary = "Create Local Document")
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/create-local-documents")
  public List<DocumentId> createLocalDocuments(@RequestBody @Valid CreateLocalDocumentsRequest request,
      @AuthenticationPrincipal AccountId accountId) {

    AccountCommandRequester requester = new AccountCommandRequester(accountId);

    List<CreateLocalDocumentsCommand.LocalDocument> localDocuments =
        request.getLocalDocuments().stream()
            .map(document -> {
              String title = Optional.ofNullable(document.getTitle()).orElse(StringUtils.EMPTY);
              return new CreateLocalDocumentsCommand.LocalDocument(
                  title, document.getContent(), document.getLocalFormedDateString());
            })
            .collect(Collectors.toList());

    CreateLocalDocumentsCommand command =
        new CreateLocalDocumentsCommand(requester, accountId, localDocuments);

    return commandGateway.request(command);
  }

  @WaitForProjection
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

  @WaitForProjection
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

  @WaitForProjection
  @Operation(summary = "Delete Documents")
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/delete-documents")
  public void deleteDocuments(@RequestBody @Valid DeleteDocumentsRequest request,
      @AuthenticationPrincipal AccountId accountId) {

    AccountCommandRequester requester = new AccountCommandRequester(accountId);
    List<DocumentId> documentIds = request.getDocumentIds().stream()
        .map(DocumentId::new)
        .collect(Collectors.toList());
    DeleteDocumentsCommand command = new DeleteDocumentsCommand(requester, documentIds);

    commandGateway.request(command);
  }

  @Operation(summary = "Send email about Documents")
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/mail-documents")
  public void mailDocuments(@RequestBody @Valid MailDocumentsRequest request,
      @AuthenticationPrincipal AccountId accountId) {

    AccountCommandRequester requester = new AccountCommandRequester(accountId);
    List<DocumentId> documentIds = request.getDocumentIds().stream()
        .map(DocumentId::new)
        .collect(Collectors.toList());
    boolean needToDelete = Boolean.TRUE.equals(request.getNeedToDelete());
    MailDocumentsCommand command = new MailDocumentsCommand(requester,
        accountId, documentIds, needToDelete);

    commandGateway.request(command, 10000);
  }
}
