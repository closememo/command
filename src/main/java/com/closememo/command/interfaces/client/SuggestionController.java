package com.closememo.command.interfaces.client;

import com.closememo.command.application.AccountCommandRequester;
import com.closememo.command.application.CommandGateway;
import com.closememo.command.application.suggestion.CreateSuggestionCommand;
import com.closememo.command.application.suggestion.DeleteSuggestionCommand;
import com.closememo.command.application.suggestion.UpdateSuggestionCommand;
import com.closememo.command.config.openapi.apitags.SuggestionApiTag;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.suggestion.SuggestionId;
import com.closememo.command.infra.projection.WaitForProjection;
import com.closememo.command.interfaces.client.requests.suggestion.CreateSuggestionRequest;
import com.closememo.command.interfaces.client.requests.suggestion.DeleteSuggestionRequest;
import com.closememo.command.interfaces.client.requests.suggestion.UpdateSuggestionRequest;
import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@SuggestionApiTag
@ClientCommandInterface
public class SuggestionController {

  private final CommandGateway commandGateway;

  public SuggestionController(CommandGateway commandGateway) {
    this.commandGateway = commandGateway;
  }

  @WaitForProjection
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/create-suggestion")
  public SuggestionId createSuggestion(@RequestBody @Valid CreateSuggestionRequest request,
      @AuthenticationPrincipal AccountId accountId) {

    AccountCommandRequester requester = new AccountCommandRequester(accountId);
    CreateSuggestionCommand command = new CreateSuggestionCommand(requester,
        accountId, request.getContent());

    return commandGateway.request(command);
  }

  @WaitForProjection
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/update-suggestion")
  public SuggestionId updateSuggestion(@RequestBody @Valid UpdateSuggestionRequest request,
      @AuthenticationPrincipal AccountId accountId) {

    AccountCommandRequester requester = new AccountCommandRequester(accountId);
    SuggestionId suggestionId = new SuggestionId(request.getSuggestionId());
    UpdateSuggestionCommand command = new UpdateSuggestionCommand(requester,
        suggestionId, accountId, request.getContent());

    return commandGateway.request(command);
  }

  @WaitForProjection
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/delete-suggestion")
  public void deleteSuggestion(@RequestBody @Valid DeleteSuggestionRequest request,
      @AuthenticationPrincipal AccountId accountId) {

    AccountCommandRequester requester = new AccountCommandRequester(accountId);
    SuggestionId suggestionId = new SuggestionId(request.getSuggestionId());
    DeleteSuggestionCommand command = new DeleteSuggestionCommand(requester, suggestionId);

    commandGateway.request(command);
  }
}
