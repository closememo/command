package com.closememo.command.interfaces.system;

import com.closememo.command.application.CommandGateway;
import com.closememo.command.application.SystemCommandRequester;
import com.closememo.command.application.notice.CreateNoticeCommand;
import com.closememo.command.application.notice.DeleteNoticeCommand;
import com.closememo.command.application.notice.UpdateNoticeCommand;
import com.closememo.command.config.openapi.apitags.SystemApiTag;
import com.closememo.command.domain.notice.NoticeId;
import com.closememo.command.interfaces.system.requests.notice.CreateNoticeRequest;
import com.closememo.command.interfaces.system.requests.notice.DeleteNoticeRequest;
import com.closememo.command.interfaces.system.requests.notice.UpdateNoticeRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@SystemApiTag
@SystemCommandInterface
public class SystemNoticeController {

  private final CommandGateway commandGateway;

  public SystemNoticeController(CommandGateway commandGateway) {
    this.commandGateway = commandGateway;
  }

  @PostMapping("/create-notice")
  public NoticeId CreateNotice(@RequestBody @Valid CreateNoticeRequest request) {
    CreateNoticeCommand command = new CreateNoticeCommand(SystemCommandRequester.getInstance(),
        request.getTitle(), request.getContent());
    return commandGateway.request(command);
  }

  @PostMapping("/update-notice")
  public NoticeId UpdateNotice(@RequestBody @Valid UpdateNoticeRequest request) {
    NoticeId noticeId = new NoticeId(request.getNoticeId());
    UpdateNoticeCommand command = new UpdateNoticeCommand(SystemCommandRequester.getInstance(),
        noticeId, request.getTitle(), request.getContent());
    return commandGateway.request(command);
  }

  @PostMapping("/delete-notice")
  public void deleteNotice(@RequestBody @Valid DeleteNoticeRequest request) {
    DeleteNoticeCommand command = new DeleteNoticeCommand(SystemCommandRequester.getInstance(),
        new NoticeId(request.getNoticeId()));
    commandGateway.request(command);
  }
}
