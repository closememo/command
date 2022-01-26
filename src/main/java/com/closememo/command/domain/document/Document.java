package com.closememo.command.domain.document;

import com.closememo.command.domain.Events;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.category.CategoryId;
import com.closememo.command.infra.persistence.converters.StringListConverter;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

@Entity
@Table(name = "documents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Document {

  private static final int NUMBER_OF_DOCUMENT_LIMIT = 500;
  private static final int NUMBER_OF_TAG_LIMIT = 100;
  private static final int MAX_TITLE_LENGTH = 100;
  private static final int MAX_CONTENT_LENGTH = 10000;
  private static final int MAX_TAG_LENGTH = 25;
  private static final String VALID_TAG_CHARS = "[_\\dA-Za-zㄱ-ㆎ가-힣ힰ-ퟆퟋ-ퟻＡ-Ｚａ-ｚｦ-ﾾￂ-ￇￊ-ￏￒ-ￗￚ-ￜ]+";

  @EmbeddedId
  private DocumentId id;
  @AttributeOverride(name = "id", column = @Column(name = "ownerId", nullable = false))
  private AccountId ownerId;
  @AttributeOverride(name = "id", column = @Column(name = "categoryId")) // TODO: 이후 nullable = false 처리
  private CategoryId categoryId;
  @Column(columnDefinition = "VARCHAR(150)")
  private String title;
  @Column(nullable = false, columnDefinition = "LONGTEXT")
  private String content;
  @Column(columnDefinition = "JSON")
  @Convert(converter = StringListConverter.class)
  private List<String> tags;
  @Column(columnDefinition = "JSON")
  @Convert(converter = StringListConverter.class)
  private List<String> autoTags;
  @Column(nullable = false)
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;
  @Embedded
  private DocumentOption option;
  @Column(nullable = false)
  private long version;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Status status;

  private Document(DocumentId id, AccountId ownerId, CategoryId categoryId, String title,
      String content, List<String> tags, List<String> autoTags, ZonedDateTime createdAt,
      DocumentOption option, long version, Status status) {

    this.id = id;
    this.ownerId = ownerId;
    this.categoryId = categoryId;
    this.title = title;
    this.content = content;
    this.tags = tags;
    this.autoTags = autoTags;
    this.createdAt = createdAt;
    this.option = option;
    this.version = version;
    this.status = status;
  }

  public static Document newOne(DocumentRepository documentRepository, AccountId ownerId,
      CategoryId categoryId, @NonNull String title, String content,
      @NonNull List<String> tags, DocumentOption option) {

    validateDocumentLimit(documentRepository, ownerId);
    validateTitle(title);
    validateContent(content);
    validateTags(tags);

    List<String> checkedTags = checkTags(tags);

    ZonedDateTime createdAt = ZonedDateTime.now();

    Document document = new Document(documentRepository.nextId(), ownerId, categoryId, title,
        content, checkedTags, Collections.emptyList(), createdAt, option, 1L, Status.NORMAL);
    Events.register(new DocumentCreatedEvent(document.getId(), ownerId, categoryId, title,
        content, checkedTags, createdAt, option, Status.NORMAL));
    return document;
  }

  public static Document newLocalOne(DocumentRepository documentRepository, AccountId ownerId,
      CategoryId categoryId, @NonNull String title, String content, ZonedDateTime createdAt) {

    validateDocumentLimit(documentRepository, ownerId);
    validateTitle(title);
    validateContent(content);

    List<String> localTags = Collections.singletonList("오프라인");
    DocumentOption option = new DocumentOption(true);

    Document document = new Document(documentRepository.nextId(), ownerId, categoryId, title,
        content, localTags, Collections.emptyList(), createdAt, option, 1L, Status.NORMAL);
    Events.register(new DocumentCreatedEvent(document.getId(), ownerId, categoryId, title,
        content, localTags, createdAt, option, Status.NORMAL));
    return document;
  }

  public void update(String title, String content, List<String> tags, DocumentOption option) {
    ZonedDateTime updatedAt = ZonedDateTime.now();

    validateTitle(title);
    validateContent(content);
    validateTags(tags);

    List<String> checkedTags = checkTags(tags);

    // 바뀐 것이 없으면 아무것도 하지 않는다.
    if (isDocumentNotChanged(title, content, checkedTags, option)) {
      return;
    }

    String previousContent = this.content;
    long previousVersion = this.version;
    DocumentOption previousOption = this.option;

    this.title = title;
    this.content = content;
    this.tags = checkedTags;
    this.updatedAt = updatedAt;
    this.option = option;
    this.version += 1;

    if (!this.option.isHasAutoTag()) {
      this.autoTags = Collections.emptyList();
    }

    Events.register(new DocumentUpdatedEvent(this.id, this.ownerId, this.title, previousContent,
        this.content, this.tags, updatedAt, previousOption, this.option, previousVersion));
  }

  private boolean isDocumentNotChanged(String title, String content, List<String> tags,
      DocumentOption option) {

    return StringUtils.equals(this.title, title)
        && StringUtils.equals(this.content, content)
        && CollectionUtils.isEqualCollection(this.tags, tags)
        && this.option.equals(option);
  }

  public void updateCategoryId(CategoryId categoryId) {
    this.categoryId = categoryId;

    Events.register(new DocumentCategoryUpdatedEvent(this.id, this.categoryId));
  }

  public void updateAutoTags(List<String> autoTags) {
    validateTags(autoTags);

    this.autoTags = autoTags;

    Events.register(new AutoTagsUpdatedEvent(this.id, this.ownerId, this.autoTags));
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
    Events.register(new DocumentDeletedEvent(this.id, this.categoryId));
  }

  public void setDeletedStatus() {
    this.status = Status.DELETED;
    Events.register(new DocumentDeletedStatusSetEvent(this.id));
  }
}
