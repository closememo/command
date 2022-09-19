package com.closememo.command.domain.difference;

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
import com.closememo.command.domain.document.DocumentId;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DifferenceTest {

  private static final String OWNER_ID = "ownerId";
  private static final String DIFFERENCE_ID = "differenceId";
  private static final String DOCUMENT_ID = "documentId";

  @Mock
  private DifferenceRepository differenceRepository;

  @Test
  @DisplayName("기등록된 Difference 개수가 이미 최대인 경우")
  public void checkDifferenceLimit() {
    when(differenceRepository.countByDocumentId(any(DocumentId.class)))
        .thenReturn((long) Difference.NUMBER_OF_DIFFERENCE_LIMIT + 1);

    assertThrows(DifferenceCountLimitExceededException.class,
        () -> Difference.newOne(differenceRepository, new AccountId(OWNER_ID), new DocumentId(DOCUMENT_ID), 0L, Collections.emptyList()));
  }

  @Test
  @DisplayName("Difference 생성")
  public void createNewDifference() {
    initializeRepository();

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      Difference difference = Difference.newOne(differenceRepository, new AccountId(OWNER_ID), new DocumentId(DOCUMENT_ID), 0L, Collections.emptyList());

      assertNotNull(difference.getId().getId());
      assertEquals(OWNER_ID, difference.getOwnerId().getId());
      assertEquals(DOCUMENT_ID, difference.getDocumentId().getId());

      mockedStatic.verify(
          () -> Events.register(any(DifferenceCreatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Difference 삭제")
  public void deleteDifference() {
    initializeRepository();

    Difference difference = Difference.newOne(differenceRepository, new AccountId(OWNER_ID), new DocumentId(DOCUMENT_ID), 0L, Collections.emptyList());

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      difference.delete();

      mockedStatic.verify(
          () -> Events.register(any(DifferenceDeletedEvent.class)), times(1));
    }
  }

  private void initializeRepository() {
    DifferenceId differenceId = mock(DifferenceId.class);
    when(differenceId.getId())
        .thenReturn(DIFFERENCE_ID);
    when(differenceRepository.nextId())
        .thenReturn(differenceId);
  }
}
