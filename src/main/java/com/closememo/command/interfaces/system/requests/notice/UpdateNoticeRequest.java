package com.closememo.command.interfaces.system.requests.notice;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateNoticeRequest {

  @NotBlank
  private String noticeId;
  @NotBlank
  private String title;
  @NotBlank
  private String content;
}
