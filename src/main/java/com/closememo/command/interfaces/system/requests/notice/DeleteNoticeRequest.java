package com.closememo.command.interfaces.system.requests.notice;

import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteNoticeRequest {

  @NotBlank
  private String noticeId;
}
