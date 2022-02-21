package com.closememo.command.application.notice;

import com.closememo.command.application.ChangeCommand;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.notice.NoticeId;
import lombok.Getter;

@Getter
public class DeleteNoticeCommand extends ChangeCommand<NoticeId> {

  private final NoticeId noticeId;

  public DeleteNoticeCommand(CommandRequester requester,
      NoticeId noticeId) {
    super(requester, noticeId);
    this.noticeId = noticeId;
  }
}
