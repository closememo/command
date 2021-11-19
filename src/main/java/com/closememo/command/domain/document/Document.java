package com.closememo.command.domain.document;

import com.closememo.command.domain.Events;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.infra.persistence.converters.StringListConverter;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
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
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.lang.NonNull;

@Entity
@Table(name = "documents")
@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Document {

  private static final int NUMBER_OF_DOCUMENT_LIMIT = 500;
  private static final int NUMBER_OF_TAG_LIMIT = 100;
  private static final int MAX_TITLE_LENGTH = 100;
  private static final int MAX_CONTENT_LENGTH = 5000;
  private static final int MAX_TAG_LENGTH = 25;
  private static final String VALID_TAG_CHARS = "[_\\dA-Za-zㄱ-ㆎ가-힣ힰ-ퟆퟋ-ퟻＡ-Ｚａ-ｚｦ-ﾾￂ-ￇￊ-ￏￒ-ￗￚ-ￜ]+";

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
      @NonNull String title, String content, @NonNull List<String> tags) {

    validateDocumentLimit(documentRepository, ownerId);
    validateTitle(title);
    validateContent(content);
    validateTags(tags);

    List<String> checkedTags = checkTags(tags);

    ZonedDateTime createdAt = ZonedDateTime.now();

    Document document = new Document(documentRepository.nextId(), ownerId,
        title, content, checkedTags, createdAt, 1L);
    Events.register(new DocumentCreatedEvent(document.getId(), ownerId,
        title, content, checkedTags, createdAt));
    return document;
  }

  public static Document newLocalOne(DocumentRepository documentRepository, AccountId ownerId,
      @NonNull String title, String content, ZonedDateTime createdAt) {

    validateDocumentLimit(documentRepository, ownerId);
    validateTitle(title);
    validateContent(content);

    List<String> localTags = Collections.singletonList("오프라인");

    Document document = new Document(documentRepository.nextId(), ownerId,
        title, content, localTags, createdAt, 1L);
    Events.register(new DocumentCreatedEvent(document.getId(), ownerId,
        title, content, localTags, createdAt));
    return document;
  }

  public void update(String title, String content, List<String> tags) {
    ZonedDateTime updatedAt = ZonedDateTime.now();

    validateTitle(title);
    validateContent(content);
    validateTags(tags);

    List<String> checkedTags = checkTags(tags);

    String previousContent = this.content;
    long previousVersion = this.version;

    this.title = title;
    this.content = content;
    this.tags = checkedTags;
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
    if (title == null) {
      return;
    }

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

  private static void validateTags(List<String> tags) {
    if (CollectionUtils.isEmpty(tags)) {
      return;
    }

    if (tags.size() > NUMBER_OF_TAG_LIMIT) {
      throw new TagCountLimitExceededException(
          String.format("the number of tags cannot exceed %d", NUMBER_OF_TAG_LIMIT));
    }

    for (String tag : tags) {
      if (tag.length() > MAX_TAG_LENGTH) {
        throw new InvalidTagException(
            String.format("tag cannot exceed %d characters", MAX_TAG_LENGTH));
      }
      if (!tag.matches(VALID_TAG_CHARS)) {
        throw new InvalidTagException("tag contains invalid characters");
      }
    }
  }

  /**
   * 중복을 제거하고 정렬된 목록을 반환한다.
   */
  private static List<String> checkTags(List<String> tags) {
    return tags.stream()
        .distinct()
        .sorted()
        .collect(Collectors.toList());
  }

  public void delete() {
    Events.register(new DocumentDeletedEvent(this.id));
  }
}
