package com.closememo.command.domain.suggestion;

import com.closememo.command.domain.Events;
import com.closememo.command.domain.account.AccountId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "suggestions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Suggestion {

  protected static final int NUMBER_OF_SUGGESTION_LIMIT = 50;
  protected static final int MAX_CONTENT_LENGTH = 1000;

  @EmbeddedId
  private SuggestionId id;
  @AttributeOverride(name = "id", column = @Column(name = "writer_id", nullable = false))
  private AccountId writerId;
  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;
  @Column(nullable = false)
  private ZonedDateTime createdAt;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Status status;

  public Suggestion(SuggestionId id, AccountId writerId, String content,
      ZonedDateTime createdAt, Status status) {
    this.id = id;
    this.writerId = writerId;
    this.content = content;
    this.createdAt = createdAt;
    this.status = status;
  }

  public static Suggestion newOne(SuggestionRepository suggestionRepository,
      AccountId writerId, String content) {

    validateSuggestionLimit(suggestionRepository, writerId);
    validateContent(content);

    ZonedDateTime createdAt = ZonedDateTime.now();

    Suggestion suggestion = new Suggestion(suggestionRepository.nextId(), writerId, content,
        createdAt, Status.REGISTERED);
    Events.register(new SuggestionCreatedEvent(suggestion.getId(), writerId, content,
        createdAt, Status.REGISTERED));
    return suggestion;
  }

  public void updateContent(String content) {
    if (!Status.REGISTERED.equals(this.status)) {
      throw new CannotChangeSuggestionException("suggestion cannot be changed");
    }

    validateContent(content);

    this.content = content;

    Events.register(new SuggestionUpdatedEvent(this.id, this.content, this.status));
  }

  public void updateStatus(Status status) {
    this.status = status;

    Events.register(new SuggestionUpdatedEvent(this.id, this.content, this.status));
  }

  public void delete() {
    Events.register(new SuggestionDeletedEvent(this.id));
  }

  public void setDeletedStatus() {
    this.status = Status.DELETED;
    Events.register(new SuggestionDeletedStatusSetEvent(this.id));
  }

  private static void validateSuggestionLimit(SuggestionRepository suggestionRepository,
      AccountId writerId) {

    long countNotDeleted = suggestionRepository.countByWriterIdAndStatusNot(writerId, Status.DELETED);
    if (countNotDeleted >= NUMBER_OF_SUGGESTION_LIMIT) {
      throw new SuggestionCountLimitExceededException(
          String.format("the number of suggestions cannot exceed %d", NUMBER_OF_SUGGESTION_LIMIT));
    }
  }

  private static void validateContent(String content) {
    if (content.length() > MAX_CONTENT_LENGTH) {
      throw new InvalidSuggestionContentException(
          String.format("content cannot exceed %d characters", MAX_CONTENT_LENGTH));
    }
  }
}
