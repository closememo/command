package com.closememo.command.interfaces.system.requests.notification;

import java.time.ZonedDateTime;
import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateNotificationRequest {

  @NotBlank
  private String title;
  @NotBlank
  private String content;
  private ZonedDateTime notifyStart;
  private ZonedDateTime notifyEnd;
}
