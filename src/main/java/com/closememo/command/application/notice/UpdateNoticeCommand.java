package com.closememo.command.application.notice;

import com.closememo.command.application.Command;
import com.closememo.command.application.CommandRequester;
import com.closememo.command.domain.notice.NoticeId;
import lombok.Getter;

@Getter
public class UpdateNoticeCommand extends Command {

  private final NoticeId noticeId;
  private final String title;
  private final String content;

  public UpdateNoticeCommand(CommandRequester requester,
      NoticeId noticeId, String title, String content) {
    super(requester);
    this.noticeId = noticeId;
    this.title = title;
    this.content = content;
  }
}
