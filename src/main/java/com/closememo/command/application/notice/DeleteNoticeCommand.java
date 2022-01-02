package com.closememo.command.application.notice;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.notice.NoticeId;
import lombok.Getter;

@Getter
public class DeleteNoticeCommand extends Command {

  private final NoticeId noticeId;

  public DeleteNoticeCommand(CommandRequester requester,
      NoticeId noticeId) {
    super(requester);
    this.noticeId = noticeId;
  }
}
