package com.closememo.command.interfaces.client;

import com.closememo.command.application.AccountCommandRequester;
import com.closememo.command.application.CommandGateway;
import com.closememo.command.application.bookmark.CreateBookmarkCommand;
import com.closememo.command.application.bookmark.DeleteBookmarkCommand;
import com.closememo.command.config.openapi.apitags.BookmarkApiTag;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.bookmark.BookmarkId;
import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.interfaces.client.requests.bookmark.CreateBookmarkRequest;
import com.closememo.command.interfaces.client.requests.bookmark.DeleteBookmarkRequest;
import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@BookmarkApiTag
@ClientCommandInterface
public class BookmarkController {

  private final CommandGateway commandGateway;

  public BookmarkController(CommandGateway commandGateway) {
    this.commandGateway = commandGateway;
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/create-bookmark")
  public BookmarkId createBookmark(@RequestBody @Valid CreateBookmarkRequest request,
      @AuthenticationPrincipal AccountId accountId) {

    CreateBookmarkCommand command = new CreateBookmarkCommand(
        new AccountCommandRequester(accountId), new DocumentId(request.getDocumentId()));
    return commandGateway.request(command);
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/delete-bookmark")
  public void deleteBookmark(@RequestBody @Valid DeleteBookmarkRequest request,
      @AuthenticationPrincipal AccountId accountId) {

    DeleteBookmarkCommand command = new DeleteBookmarkCommand(
        new AccountCommandRequester(accountId), new DocumentId(request.getDocumentId()));
    commandGateway.request(command);
  }
}
