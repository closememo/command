package com.closememo.command.interfaces.system;

import com.closememo.command.application.CommandGateway;
import com.closememo.command.application.SystemCommandRequester;
import com.closememo.command.application.account.WithdrawAccountCommand;
import com.closememo.command.config.openapi.apitags.SystemApiTag;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.interfaces.system.requests.account.DeleteAccountRequest;
import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@SystemApiTag
@SystemCommandInterface
public class SystemAccountController {

  private final CommandGateway commandGateway;

  public SystemAccountController(CommandGateway commandGateway) {
    this.commandGateway = commandGateway;
  }

  @PreAuthorize("hasRole('SYSTEM')")
  @PostMapping("/delete-account")
  public void deleteAccount(@RequestBody @Valid DeleteAccountRequest request) {
    WithdrawAccountCommand command = new WithdrawAccountCommand(
        SystemCommandRequester.getInstance(), new AccountId(request.getAccountId()));
    commandGateway.request(command);
  }
}
