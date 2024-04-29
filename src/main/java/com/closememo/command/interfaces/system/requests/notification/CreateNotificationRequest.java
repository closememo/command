package com.closememo.command.interfaces.system.requests.notification;

import jakarta.validation.constraints.NotBlank;
import java.time.ZonedDateTime;
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
