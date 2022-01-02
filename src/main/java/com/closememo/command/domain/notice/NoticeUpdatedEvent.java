package com.closememo.command.domain.notice;

import com.closememo.command.domain.DomainEvent;
import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
public class NoticeUpdatedEvent extends DomainEvent {

  private static final long serialVersionUID = -7516150455970552422L;

  private final String title;
  private final String content;
  private final ZonedDateTime updatedAt;

  public NoticeUpdatedEvent(NoticeId noticeId, String title, String content,
      ZonedDateTime updatedAt) {
    super(noticeId.getId(), 1);
    this.title = title;
    this.content = content;
    this.updatedAt = updatedAt;
  }
}
