package com.closememo.command.domain.notice;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class NoticeDeletedEvent extends DomainEvent {

  private static final long serialVersionUID = -586940156492097124L;

  public NoticeDeletedEvent(NoticeId noticeId) {
    super(noticeId.getId(), 1);
  }
}
