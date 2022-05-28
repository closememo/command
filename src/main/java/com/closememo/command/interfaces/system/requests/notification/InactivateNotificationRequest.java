package com.closememo.command.interfaces.system.requests.notification;

import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class InactivateNotificationRequest {

  @NotBlank
  private String notificationId;
}
