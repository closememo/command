package com.closememo.command.domain.document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.closememo.command.domain.Events;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.category.CategoryId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentTest {

  private static final String DOCUMENT_ID = "documentId";
  private static final String OWNER_ID = "ownerId";
  private static final String CATEGORY_ID = "categoryId";

  @Mock
  private DocumentRepository documentRepository;

  @Test
  @DisplayName("기등록된 Document 개수가 이미 최대인 경우")
  public void checkDocumentLimit() {
    DocumentOption documentOption = mock(DocumentOption.class);

    when(documentRepository.countByOwnerId(any(AccountId.class)))
        .thenReturn((long) Document.NUMBER_OF_DOCUMENT_LIMIT);

    assertThrows(DocumentCountLimitExceededException.class,
        () -> Document.newOne(documentRepository, new AccountId(OWNER_ID),
            new CategoryId(CATEGORY_ID), "title", "content", Collections.emptyList(), documentOption));
  }

  @Test
  @DisplayName("생성시 title 길이 검사")
  public void checkSizeOfTitleWhenCreating() {
    String longTitle = "x".repeat(Document.MAX_TITLE_LENGTH) + "a";

    DocumentOption documentOption = mock(DocumentOption.class);

    assertThrows(InvalidTitleException.class,
        () -> Document.newOne(documentRepository, new AccountId(OWNER_ID),
            new CategoryId(CATEGORY_ID), longTitle, "content", Collections.emptyList(), documentOption));
  }

  @Test
  @DisplayName("생성시 content 길이 검사")
  public void checkSizeOfContentWhenCreating() {
    String longContent = "x".repeat(Document.MAX_CONTENT_LENGTH) + "a";

    DocumentOption documentOption = mock(DocumentOption.class);

    assertThrows(InvalidContentException.class,
        () -> Document.newOne(documentRepository, new AccountId(OWNER_ID),
            new CategoryId(CATEGORY_ID), "title", longContent, Collections.emptyList(), documentOption));
  }

  @Test
  @DisplayName("생성시 tag 최대 개수 검사")
  public void checkNumberOfTagsWhenCreating() {
    List<String> tags = spy(new ArrayList<>());
    when(tags.isEmpty())
        .thenReturn(Boolean.FALSE);
    when(tags.size())
        .thenReturn(Document.NUMBER_OF_TAG_LIMIT + 1);

    DocumentOption documentOption = mock(DocumentOption.class);

    assertThrows(TagCountLimitExceededException.class,
        () -> Document.newOne(documentRepository, new AccountId(OWNER_ID),
            new CategoryId(CATEGORY_ID), "title", "content", tags, documentOption));
  }

  @Test
  @DisplayName("생성시 tag 이름 검사 - 이름이 너무 길면 예외 발생")
  public void checkSizeOfTagNameWhenCreating() {
    List<String> tags = new ArrayList<>();
    tags.add("x".repeat(Document.MAX_TAG_LENGTH) + "a");

    DocumentOption documentOption = mock(DocumentOption.class);

    assertThrows(InvalidTagException.class,
        () -> Document.newOne(documentRepository, new AccountId(OWNER_ID),
            new CategoryId(CATEGORY_ID), "title", "content", tags, documentOption));
  }

  @Test
  @DisplayName("생성시 tag 이름 검사 - 허용되지 않는 문자 포함시 예외 발생")
  public void checkTagNameWithInvalidCharWhenCreating() {
    List<String> tags = new ArrayList<>();
    tags.add("\uD83E\uDD17");

    DocumentOption documentOption = mock(DocumentOption.class);

    assertThrows(InvalidTagException.class,
        () -> Document.newOne(documentRepository, new AccountId(OWNER_ID),
            new CategoryId(CATEGORY_ID), "title", "content", tags, documentOption));
  }

  @Test
  @DisplayName("새 Document 생성")
  public void createNewDocument() {
    initializeDocumentRepository();

    DocumentOption documentOption = mock(DocumentOption.class);

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      Document document = Document.newOne(documentRepository, new AccountId(OWNER_ID),
          new CategoryId(CATEGORY_ID), "title", "content", Collections.emptyList(), documentOption);

      assertNotNull(document.getId().getId());
      assertEquals(OWNER_ID, document.getOwnerId().getId());
      assertEquals(CATEGORY_ID, document.getCategoryId().getId());
      assertEquals("title", document.getTitle());
      assertEquals("content", document.getContent());
      assertNotNull(document.getTags());
      assertNotNull(document.getAutoTags());
      assertNotNull(document.getOption());
      assertEquals(1L, document.getVersion());
      assertEquals(Status.NORMAL, document.getStatus());

      mockedStatic.verify(
          () -> Events.register(any(DocumentCreatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("새 Document 생성 - 오프라인")
  public void createNewLocalDocument() {
    initializeDocumentRepository();

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      Document document = Document.newLocalOne(documentRepository, new AccountId(OWNER_ID),
          new CategoryId(CATEGORY_ID), "title", "content", ZonedDateTime.now());

      assertNotNull(document.getId().getId());
      assertEquals(OWNER_ID, document.getOwnerId().getId());
      assertEquals(CATEGORY_ID, document.getCategoryId().getId());
      assertEquals("title", document.getTitle());
      assertEquals("content", document.getContent());
      assertNotNull(document.getTags());
      assertNotNull(document.getAutoTags());
      assertNotNull(document.getOption());
      assertEquals(1L, document.getVersion());
      assertEquals(Status.NORMAL, document.getStatus());

      assertEquals(1, document.getTags().size());
      assertEquals(Document.LOCAL_DOCUMENT_TAG, document.getTags().get(0));

      mockedStatic.verify(
          () -> Events.register(any(DocumentCreatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Document 생성 시 tag 이름 중복 삭제 및 정렬")
  public void sortTagsWhenCreating() {
    initializeDocumentRepository();

    List<String> origTags = new ArrayList<>();
    origTags.add("tiger");
    origTags.add("zebra");
    origTags.add("tiger");
    origTags.add("alpaca");
    DocumentOption documentOption = mock(DocumentOption.class);

    Document document = Document.newOne(documentRepository, new AccountId(OWNER_ID),
        new CategoryId(CATEGORY_ID), "title", "content", origTags, documentOption);

    List<String> tags = document.getTags();
    // 중복된 태그를 제거하여 3 개가 되어야 함
    assertEquals(3, tags.size());
    // 정렬되어야 함
    assertEquals("alpaca", tags.get(0));
    assertEquals("zebra", tags.get(tags.size() - 1));
  }

  @Test
  @DisplayName("Document 수정")
  public void updateDocument() {
    initializeDocumentRepository();

    DocumentOption documentOption = mock(DocumentOption.class);

    Document document = Document.newOne(documentRepository, new AccountId(OWNER_ID),
        new CategoryId(CATEGORY_ID), "title", "content", Collections.emptyList(), documentOption);

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      String newTitle = "newTitle";
      String newContent = "newContent";
      List<String> newTags = List.of("newTag");
      DocumentOption newOption = mock(DocumentOption.class);
      when(newOption.isHasAutoTag())
          .thenReturn(Boolean.TRUE);

      document.update(newTitle, newContent, newTags, newOption);

      assertEquals(newTitle, document.getTitle());
      assertEquals(newContent, document.getContent());
      assertEquals(1, document.getTags().size());
      assertEquals("newTag", document.getTags().get(0));
      assertEquals(Boolean.TRUE, document.getOption().isHasAutoTag());

      mockedStatic.verify(
          () -> Events.register(any(DocumentUpdatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Document CategoryId 수정")
  public void updateDocumentCategoryId() {
    initializeDocumentRepository();

    DocumentOption documentOption = mock(DocumentOption.class);

    Document document = Document.newOne(documentRepository, new AccountId(OWNER_ID),
        new CategoryId(CATEGORY_ID), "title", "content", Collections.emptyList(), documentOption);

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      document.updateCategoryId(new CategoryId("newCategoryId"));

      assertEquals("newCategoryId", document.getCategoryId().getId());

      mockedStatic.verify(
          () -> Events.register(any(DocumentCategoryUpdatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Document autoTags 수정")
  public void updateDocumentAutoTags() {
    initializeDocumentRepository();

    DocumentOption documentOption = mock(DocumentOption.class);

    Document document = Document.newOne(documentRepository, new AccountId(OWNER_ID),
        new CategoryId(CATEGORY_ID), "title", "content", Collections.emptyList(), documentOption);

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      List<String> newTags = new ArrayList<>();
      newTags.add("newTags1");
      newTags.add("newTags2");

      document.updateAutoTags(newTags);

      assertEquals(2, document.getAutoTags().size());
      assertEquals("newTags1", document.getAutoTags().get(0));

      mockedStatic.verify(
          () -> Events.register(any(AutoTagsUpdatedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Document Difference 모두 삭제")
  public void clearDifferences() {
    initializeDocumentRepository();

    DocumentOption documentOption = mock(DocumentOption.class);

    Document document = Document.newOne(documentRepository, new AccountId(OWNER_ID),
        new CategoryId(CATEGORY_ID), "title", "content", Collections.emptyList(), documentOption);

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      document.clearDifferences();

      mockedStatic.verify(
          () -> Events.register(any(DocumentDifferencesClearedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Document 삭제")
  public void deleteDocument() {
    initializeDocumentRepository();

    DocumentOption documentOption = mock(DocumentOption.class);

    Document document = Document.newOne(documentRepository, new AccountId(OWNER_ID),
        new CategoryId(CATEGORY_ID), "title", "content", Collections.emptyList(), documentOption);

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      document.delete();

      mockedStatic.verify(
          () -> Events.register(any(DocumentDeletedEvent.class)), times(1));
    }
  }

  @Test
  @DisplayName("Document 의 status 를 deleted 로 설정")
  public void setDeletedStatus() {
    initializeDocumentRepository();

    DocumentOption documentOption = mock(DocumentOption.class);

    Document document = Document.newOne(documentRepository, new AccountId(OWNER_ID),
        new CategoryId(CATEGORY_ID), "title", "content", Collections.emptyList(), documentOption);

    try (MockedStatic<Events> mockedStatic = mockStatic(Events.class)) {
      document.setDeletedStatus();

      assertEquals(Status.DELETED, document.getStatus());

      mockedStatic.verify(
          () -> Events.register(any(DocumentDeletedStatusSetEvent.class)), times(1));
    }
  }

  private void initializeDocumentRepository() {
    DocumentId documentId = mock(DocumentId.class);
    when(documentId.getId())
        .thenReturn(DOCUMENT_ID);
    when(documentRepository.nextId())
        .thenReturn(documentId);
    when(documentRepository.countByOwnerId(any(AccountId.class)))
        .thenReturn(0L);
  }
}
