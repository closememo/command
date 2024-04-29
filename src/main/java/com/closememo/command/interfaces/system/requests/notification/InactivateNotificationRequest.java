package com.closememo.command.interfaces.system.requests.notification;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class InactivateNotificationRequest {

  @NotBlank
  private String notificationId;
}
