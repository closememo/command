package com.closememo.command.domain.notice;

import com.closememo.command.domain.Events;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {

  @EmbeddedId
  private NoticeId id;
  @Column(nullable = false, columnDefinition = "VARCHAR(150)")
  private String title;
  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;
  @Column(nullable = false)
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;

  public Notice(NoticeId id, String title, String content, ZonedDateTime createdAt) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.createdAt = createdAt;
  }

  public static Notice newOne(NoticeId id, String title, String content) {
    ZonedDateTime createdAt = ZonedDateTime.now();

    Notice notice = new Notice(id, title, content, createdAt);

    Events.register(new NoticeCreatedEvent(notice.getId(), title, content, createdAt));
    return notice;
  }

  public void update(String title, String content) {
    ZonedDateTime updatedAt = ZonedDateTime.now();

    this.title = title;
    this.content = content;
    this.updatedAt = updatedAt;

    Events.register(new NoticeUpdatedEvent(this.id, this.title, this.content, this.updatedAt));
  }

  public void delete() {
    Events.register(new NoticeDeletedEvent(this.id));
  }
}
