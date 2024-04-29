package com.closememo.command.interfaces.system.requests.notice;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateNoticeRequest {

  @NotBlank
  private String title;
  @NotBlank
  private String content;
}
