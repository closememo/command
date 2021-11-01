package com.closememo.command.domain.document;

import com.closememo.command.domain.Events;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.infra.persistence.converters.StringListConverter;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "documents")
@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Document {

  private static final long NUMBER_OF_DOCUMENT_LIMIT = 500;
  private static final int MAX_TITLE_LENGTH = 100;
  private static final int MAX_CONTENT_LENGTH = 5000;

  @EmbeddedId
  private DocumentId id;
  @AttributeOverride(name = "id", column = @Column(name = "ownerId", nullable = false))
  private AccountId ownerId;
  @Column(columnDefinition = "VARCHAR(150)")
  private String title;
  @Column(nullable = false, columnDefinition = "LONGTEXT")
  private String content;
  @Column(columnDefinition = "JSON")
  @Convert(converter = StringListConverter.class)
  private List<String> tags;
  @Column(nullable = false)
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;
  @Column(nullable = false)
  private long version;

  public Document(DocumentId id, AccountId ownerId, String title, String content, List<String> tags,
      ZonedDateTime createdAt, long version) {
    this.id = id;
    this.ownerId = ownerId;
    this.title = title;
    this.content = content;
    this.tags = tags;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static Document newOne(DocumentRepository documentRepository, AccountId ownerId,
      String title, String content, List<String> tags) {

    validateDocumentLimit(documentRepository, ownerId);
    validateTitle(title);
    validateContent(content);

    if (tags == null) {
      tags = Collections.emptyList();
    }
    ZonedDateTime createdAt = ZonedDateTime.now();

    Document document = new Document(documentRepository.nextId(), ownerId,
        title, content, tags, createdAt, 1L);
    Events.register(new DocumentCreatedEvent(document.getId(), ownerId,
        title, content, tags, createdAt));
    return document;
  }

  public void update(String title, String content, List<String> tags) {
    ZonedDateTime updatedAt = ZonedDateTime.now();

    validateTitle(title);
    validateContent(content);

    String previousContent = this.content;
    long previousVersion = this.version;

    this.title = title;
    this.content = content;
    this.tags = tags;
    this.updatedAt = updatedAt;
    this.version += 1;

    Events.register(new DocumentUpdatedEvent(this.id, this.ownerId, this.title, previousContent,
        this.content, this.tags, updatedAt, previousVersion));
  }

  private static void validateDocumentLimit(DocumentRepository documentRepository,
      AccountId ownerId) {
    if (documentRepository.countByOwnerId(ownerId) >= NUMBER_OF_DOCUMENT_LIMIT) {
      throw new DocumentCountLimitExceededException(
          String.format("the number of documents cannot exceed %d", NUMBER_OF_DOCUMENT_LIMIT));
    }
  }

  private static void validateTitle(String title) {
    if (title.length() > MAX_TITLE_LENGTH) {
      throw new InvalidTitleException(
          String.format("title cannot exceed %d characters", MAX_TITLE_LENGTH));
    }
  }

  private static void validateContent(String content) {
    if (content.length() > MAX_CONTENT_LENGTH) {
      throw new InvalidContentException(
          String.format("content cannot exceed %d characters", MAX_CONTENT_LENGTH));
    }
  }

  public void delete() {
    Events.register(new DocumentDeletedEvent(this.id));
  }
}
