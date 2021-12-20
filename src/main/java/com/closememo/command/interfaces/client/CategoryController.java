package com.closememo.command.interfaces.client;

import com.closememo.command.application.AccountCommandRequester;
import com.closememo.command.application.CommandGateway;
import com.closememo.command.application.category.CreateCategoryCommand;
import com.closememo.command.application.category.DeleteCategoryCommand;
import com.closememo.command.application.category.UpdateCategoryCommand;
import com.closememo.command.config.openapi.apitags.CategoryApiTag;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.category.CategoryId;
import com.closememo.command.infra.projection.WaitForProjection;
import com.closememo.command.interfaces.client.requests.category.CreateCategoryRequest;
import com.closememo.command.interfaces.client.requests.category.DeleteCategoryRequest;
import com.closememo.command.interfaces.client.requests.category.UpdateCategoryRequest;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Optional;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CategoryApiTag
@ClientCommandInterface
public class CategoryController {

  private final CommandGateway commandGateway;

  public CategoryController(CommandGateway commandGateway) {
    this.commandGateway = commandGateway;
  }

  @WaitForProjection
  @Operation(summary = "Create Category")
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/create-category")
  public CategoryId createCategory(@RequestBody @Valid CreateCategoryRequest request,
      @AuthenticationPrincipal AccountId accountId) {

    String name = Optional.ofNullable(request.getName()).orElse(StringUtils.EMPTY);

    AccountCommandRequester requester = new AccountCommandRequester(accountId);
    CategoryId parentId = new CategoryId(request.getParentId());
    CreateCategoryCommand command = new CreateCategoryCommand(requester, accountId, name, parentId);

    return commandGateway.request(command);
  }

  @WaitForProjection
  @Operation(summary = "Update Category")
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/update-category")
  public CategoryId updateCategory(@RequestBody @Valid UpdateCategoryRequest request,
      @AuthenticationPrincipal AccountId accountId) {

    String name = Optional.ofNullable(request.getName()).orElse(StringUtils.EMPTY);

    AccountCommandRequester requester = new AccountCommandRequester(accountId);
    CategoryId categoryId = new CategoryId(request.getCategoryId());
    UpdateCategoryCommand command = new UpdateCategoryCommand(requester, categoryId, name);

    return commandGateway.request(command);
  }

  @WaitForProjection
  @Operation(summary = "Delete Category")
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/delete-category")
  public void deleteCategory(@RequestBody @Valid DeleteCategoryRequest request,
      @AuthenticationPrincipal AccountId accountId) {

    AccountCommandRequester requester = new AccountCommandRequester(accountId);
    CategoryId categoryId = new CategoryId(request.getCategoryId());
    DeleteCategoryCommand command = new DeleteCategoryCommand(requester, categoryId);

    commandGateway.request(command);
  }
}
