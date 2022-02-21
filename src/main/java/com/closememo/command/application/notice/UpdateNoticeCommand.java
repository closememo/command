package com.closememo.command.application.notice;

import com.closememo.command.application.ChangeCommand;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.notice.NoticeId;
import lombok.Getter;

@Getter
public class UpdateNoticeCommand extends ChangeCommand<NoticeId> {

  private final NoticeId noticeId;
  private final String title;
  private final String content;

  public UpdateNoticeCommand(CommandRequester requester,
      NoticeId noticeId, String title, String content) {
    super(requester, noticeId);
    this.noticeId = noticeId;
    this.title = title;
    this.content = content;
  }
}
