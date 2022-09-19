package com.closememo.command.domain.suggestion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.closememo.command.domain.Events;
import com.closememo.command.domain.account.AccountId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SuggestionTest {

  private static final String WRITER_ID = "writerId";
  private static final String SUGGESTION_ID = "suggestionId";

  @Mock
  private SuggestionRepository suggestionRepository;

  @Test
  @DisplayName("생성시 기등록된 Suggestion 개수 확인")
  public void validateSuggestionLimitWhenCreating() {
    when(suggestionRepository.countByWriterIdAndStatusNot(any(AccountId.class), any()))
        .thenReturn((long) Suggestion.NUMBER_OF_SUGGESTION_LIMIT);

    assertThrows(SuggestionCountLimitExceededException.class,
        () -> Suggestion.newOne(suggestionRepository, new AccountId(WRITER_ID), "content"));
  }

  @Test
  @DisplayName("생성시 Suggestion content 확인")
  public void validateSuggestionContentWhenCreating() {
    String content = "x".repeat(Suggestion.MAX_CONTENT_LENGTH) + "a";

    when(suggestionRepository.countByWriterIdAndStatusNot(any(AccountId.class), any()))
        .thenReturn(0L);

    assertThrows(InvalidSuggestionContentException.class,
        () -> Suggestion.newOne(suggestionRepository, new AccountId(WRITER_ID), content));
  }

  @Test
  @DisplayName("새 Suggestion 생성")
  public void createNewSuggestion() {
    initializeSuggestionRepository();

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      Suggestion suggestion = Suggestion.newOne(suggestionRepository, new AccountId(WRITER_ID), "content");

      assertNotNull(suggestion.getId().getId());
      assertEquals("content", suggestion.getContent());
      assertEquals(Status.REGISTERED, suggestion.getStatus());

      mockedStatic.verify(
          () -> Events.register(any(SuggestionCreatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Suggestion content 수정")
  public void updateSuggestionContent() {
    initializeSuggestionRepository();

    Suggestion suggestion = Suggestion.newOne(suggestionRepository, new AccountId(WRITER_ID), "content");

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      suggestion.updateContent("newContent");

      assertEquals("newContent", suggestion.getContent());

      mockedStatic.verify(
          () -> Events.register(any(SuggestionUpdatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Suggestion status 수정")
  public void updateSuggestionStatus() {
    initializeSuggestionRepository();

    Suggestion suggestion = Suggestion.newOne(suggestionRepository, new AccountId(WRITER_ID), "content");

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      suggestion.updateStatus(Status.CHECKED);

      assertEquals(Status.CHECKED, suggestion.getStatus());

      mockedStatic.verify(
          () -> Events.register(any(SuggestionUpdatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Suggestion 삭제")
  public void deleteSuggestion() {
    initializeSuggestionRepository();

    Suggestion suggestion = Suggestion.newOne(suggestionRepository, new AccountId(WRITER_ID), "content");

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      suggestion.delete();

      mockedStatic.verify(
          () -> Events.register(any(SuggestionDeletedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Suggestion status 를 DELETED 로 설정")
  public void setSuggestionStatusDeleted() {
    initializeSuggestionRepository();

    Suggestion suggestion = Suggestion.newOne(suggestionRepository, new AccountId(WRITER_ID), "content");

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      suggestion.setDeletedStatus();

      assertEquals(Status.DELETED, suggestion.getStatus());

      mockedStatic.verify(
          () -> Events.register(any(SuggestionDeletedStatusSetEvent.class)), times(1));
    }
  }

  private void initializeSuggestionRepository() {
    SuggestionId suggestionId = mock(SuggestionId.class);
    when(suggestionId.getId())
        .thenReturn(SUGGESTION_ID);
    when(suggestionRepository.nextId())
        .thenReturn(suggestionId);
    when(suggestionRepository.countByWriterIdAndStatusNot(any(AccountId.class), any()))
        .thenReturn(0L);
  }
}