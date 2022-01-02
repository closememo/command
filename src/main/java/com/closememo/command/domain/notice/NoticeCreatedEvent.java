package com.closememo.command.domain.notice;

import com.closememo.command.domain.DomainEvent;
import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
public class NoticeCreatedEvent extends DomainEvent {

  private static final long serialVersionUID = 6983223718426926357L;

  private final String title;
  private final String content;
  private final ZonedDateTime createdAt;

  public NoticeCreatedEvent(NoticeId noticeId, String title, String content,
      ZonedDateTime createdAt) {
    super(noticeId.getId(), 1);
    this.title = title;
    this.content = content;
    this.createdAt = createdAt;
  }
}
