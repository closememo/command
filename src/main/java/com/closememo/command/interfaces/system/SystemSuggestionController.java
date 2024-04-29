package com.closememo.command.interfaces.system;

import com.closememo.command.application.CommandGateway;
import com.closememo.command.application.SystemCommandRequester;
import com.closememo.command.application.suggestion.ChangeSuggestionStatusCommand;
import com.closememo.command.domain.suggestion.Status;
import com.closememo.command.domain.suggestion.SuggestionId;
import com.closememo.command.interfaces.system.requests.suggestion.ChangeSuggestionStatusRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@SystemCommandInterface
public class SystemSuggestionController {

  private final CommandGateway commandGateway;

  public SystemSuggestionController(CommandGateway commandGateway) {
    this.commandGateway = commandGateway;
  }

  @PostMapping("/change-suggestion-status")
  public void changeSuggestionStatus(@RequestBody @Valid ChangeSuggestionStatusRequest request) {
    SuggestionId suggestionId = new SuggestionId(request.getSuggestionId());
    ChangeSuggestionStatusCommand command = new ChangeSuggestionStatusCommand(
        SystemCommandRequester.getInstance(), suggestionId, Status.valueOf(request.getStatus()));
    commandGateway.request(command);
  }
}
