package com.closememo.command.interfaces.client;

import com.closememo.command.application.AccountCommandRequester;
import com.closememo.command.application.AnonymousCommandRequester;
import com.closememo.command.application.CommandGateway;
import com.closememo.command.application.account.ClearTokensCommand;
import com.closememo.command.application.account.LoginAccount;
import com.closememo.command.application.account.LoginNaverAccountCommand;
import com.closememo.command.application.account.LogoutCommand;
import com.closememo.command.application.account.RegisterNaverAccountCommand;
import com.closememo.command.application.account.ReissueTokenCommand;
import com.closememo.command.application.account.UpdateAccountOptionCommand;
import com.closememo.command.application.account.WithdrawAccountCommand;
import com.closememo.command.config.openapi.apitags.AccountApiTag;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.interfaces.client.requests.account.ClearTokensRequest;
import com.closememo.command.interfaces.client.requests.account.LogoutRequest;
import com.closememo.command.interfaces.client.requests.account.NaverAccountRequest;
import com.closememo.command.interfaces.client.requests.account.ReissueTokenRequest;
import com.closememo.command.interfaces.client.requests.account.UpdateAccountOptionRequest;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@AccountApiTag
@ClientCommandInterface
public class AccountController {

  private final CommandGateway commandGateway;

  public AccountController(CommandGateway commandGateway) {
    this.commandGateway = commandGateway;
  }

  @Operation(summary = "Register with NAVER Account")
  @PostMapping("/register-naver-account")
  public LoginAccount registerNaverAccount(@RequestBody @Valid NaverAccountRequest request) {
    RegisterNaverAccountCommand command =
        new RegisterNaverAccountCommand(AnonymousCommandRequester.getInstance(),
            request.getCode(), request.getState());
    return commandGateway.request(command);
  }

  @Operation(summary = "Login with NAVER Account")
  @PostMapping("/login-naver-account")
  public LoginAccount loginNaverAccount(@RequestBody @Valid NaverAccountRequest request) {
    LoginNaverAccountCommand command =
        new LoginNaverAccountCommand(AnonymousCommandRequester.getInstance(),
            request.getCode(), request.getState());
    return commandGateway.request(command);
  }

  @Operation(summary = "Reissue token")
  @PostMapping("/reissue-token")
  public LoginAccount reissueToken(@RequestBody @Valid ReissueTokenRequest request,
      @AuthenticationPrincipal AccountId accountId) {
    ReissueTokenCommand command =
        new ReissueTokenCommand(new AccountCommandRequester(accountId), request.getTokenId());
    return commandGateway.request(command);
  }

  @Operation(summary = "Logout")
  @PostMapping("/logout")
  public AccountId logout(@RequestBody @Valid LogoutRequest request,
      @AuthenticationPrincipal AccountId accountId) {
    LogoutCommand command =
        new LogoutCommand(new AccountCommandRequester(accountId), accountId, request.getTokenId());
    return commandGateway.request(command);
  }

  @Operation(summary = "Clear tokens")
  @PostMapping("/clear-tokens")
  public AccountId clearTokens(@RequestBody @Valid ClearTokensRequest request) {
    AccountId accountId = new AccountId(request.getAccountId());
    ClearTokensCommand command =
        new ClearTokensCommand(AnonymousCommandRequester.getInstance(), accountId);
    return commandGateway.request(command);
  }

  @Operation(summary = "Withdraw NAVER Account")
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/withdraw")
  public void withdraw(@AuthenticationPrincipal AccountId accountId) {
    WithdrawAccountCommand command =
        new WithdrawAccountCommand(new AccountCommandRequester(accountId), accountId);
    commandGateway.request(command);
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/update-account-option")
  public void updateAccountOption(@RequestBody UpdateAccountOptionRequest request,
      @AuthenticationPrincipal AccountId accountId) {

    UpdateAccountOptionCommand command =
        new UpdateAccountOptionCommand(new AccountCommandRequester(accountId), accountId,
            request.getDocumentOrderType(), request.getDocumentCount());
    commandGateway.request(command);
  }
}
