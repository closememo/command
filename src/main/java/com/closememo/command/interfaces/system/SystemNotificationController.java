package com.closememo.command.interfaces.system;

import com.closememo.command.application.CommandGateway;
import com.closememo.command.application.SystemCommandRequester;
import com.closememo.command.application.notification.ActivateNotificationCommand;
import com.closememo.command.application.notification.CreateNotificationCommand;
import com.closememo.command.application.notification.DeleteNotificationCommand;
import com.closememo.command.application.notification.InactivateNotificationCommand;
import com.closememo.command.application.notification.UpdateNotificationCommand;
import com.closememo.command.config.openapi.apitags.SystemApiTag;
import com.closememo.command.domain.notification.NotificationId;
import com.closememo.command.interfaces.system.requests.notification.ActivateNotificationRequest;
import com.closememo.command.interfaces.system.requests.notification.CreateNotificationRequest;
import com.closememo.command.interfaces.system.requests.notification.DeleteNotificationRequest;
import com.closememo.command.interfaces.system.requests.notification.InactivateNotificationRequest;
import com.closememo.command.interfaces.system.requests.notification.UpdateNotificationRequest;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@SystemApiTag
@SystemCommandInterface
public class SystemNotificationController {

  private final CommandGateway commandGateway;

  public SystemNotificationController(CommandGateway commandGateway) {
    this.commandGateway = commandGateway;
  }

  @PostMapping("/create-notification")
  public NotificationId createNotification(@RequestBody @Valid CreateNotificationRequest request) {
    CreateNotificationCommand command = new CreateNotificationCommand(
        SystemCommandRequester.getInstance(), request.getTitle(), request.getContent(),
        request.getNotifyStart(), request.getNotifyEnd());
    return commandGateway.request(command);
  }

  @PostMapping("/update-notification")
  public NotificationId updateNotification(@RequestBody @Valid UpdateNotificationRequest request) {
    NotificationId notificationId = new NotificationId(request.getNotificationId());
    UpdateNotificationCommand command = new UpdateNotificationCommand(
        SystemCommandRequester.getInstance(), notificationId, request.getTitle(),
        request.getContent(), request.getNotifyStart(), request.getNotifyEnd());
    return commandGateway.request(command);
  }

  @PostMapping("/activate-notification")
  public NotificationId activateNotification(
      @RequestBody @Valid ActivateNotificationRequest request) {

    NotificationId notificationId = new NotificationId(request.getNotificationId());
    ActivateNotificationCommand command = new ActivateNotificationCommand(
        SystemCommandRequester.getInstance(), notificationId);
    return commandGateway.request(command);
  }

  @PostMapping("/inactivate-notification")
  public NotificationId inactivateNotification(
      @RequestBody @Valid InactivateNotificationRequest request) {

    NotificationId notificationId = new NotificationId(request.getNotificationId());
    InactivateNotificationCommand command = new InactivateNotificationCommand(
        SystemCommandRequester.getInstance(), notificationId);
    return commandGateway.request(command);
  }

  @PostMapping("/delete-notification")
  public void deleteNotification(@RequestBody @Valid DeleteNotificationRequest request) {
    NotificationId notificationId = new NotificationId(request.getNotificationId());
    DeleteNotificationCommand command = new DeleteNotificationCommand(
        SystemCommandRequester.getInstance(), notificationId);
    commandGateway.request(command);
  }
}
