package com.closememo.command.interfaces.client.requests.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateCategoryRequest {

  @NotBlank
  private String name;
  @NotBlank
  private String parentId;
}
