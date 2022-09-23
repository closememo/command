package com.closememo.command.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.bookmark.Bookmark;
import com.closememo.command.domain.bookmark.BookmarkId;
import com.closememo.command.domain.bookmark.BookmarkRepository;
import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.infra.persistence.imports.BookmarkJpaRepository;
import com.closememo.command.test.ImportSequenceGenerator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.CollectionUtils;

@ImportSequenceGenerator
@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class BookmarkRepositoryImplTest {

  private static final String OWNER_ID = "ownerId";
  private static final String DOCUMENT_ID = "documentId";

  @Autowired
  private BookmarkJpaRepository bookmarkJpaRepository;
  @Autowired
  private SequenceGenerator sequenceGenerator;
  private BookmarkRepository bookmarkRepository;

  @BeforeEach
  public void beforeEach() {
    bookmarkRepository = new BookmarkRepositoryImpl(sequenceGenerator, bookmarkJpaRepository);
  }

  @AfterEach
  public void afterEach() {
    bookmarkJpaRepository.deleteAll();
  }

  @Test
  @DisplayName("BookmarkId 생성")
  public void createBookmarkId() {
    BookmarkId bookmarkId = bookmarkRepository.nextId();
    assertNotNull(bookmarkId);
  }

  @Test
  @DisplayName("Bookmark 저장 후 findByDocumentId")
  public void saveAndFindByDocumentId() {
    AccountId accountId = new AccountId(OWNER_ID);
    DocumentId documentId = new DocumentId(DOCUMENT_ID);
    Bookmark bookmark = Bookmark.newBookmark(bookmarkRepository, accountId, documentId);
    // 저장
    bookmarkRepository.save(bookmark);
    // findByDocumentId
    Optional<Bookmark> optionalBookmark = bookmarkRepository.findByDocumentId(documentId);
    assertTrue(optionalBookmark.isPresent());
    // 조회한 Bookmark 값 확인
    Bookmark saved = optionalBookmark.get();
    assertEquals(accountId, saved.getOwnerId());
    assertEquals(documentId, saved.getDocumentId());
    // 삭제 후 확인
    bookmarkRepository.delete(saved);
    assertTrue(bookmarkRepository.findByDocumentId(documentId).isEmpty());
  }

  @Test
  @DisplayName("동일한 AccountId 에 대한 복수의 Bookmark 저장 후 findAllByOwnerId")
  public void saveBookmarksAndFindByOwnerId() {
    int num = 10;

    AccountId accountId = new AccountId(OWNER_ID);
    List<DocumentId> documentIds = IntStream.range(1, num + 1)
        .mapToObj(i -> DOCUMENT_ID + i)
        .map(DocumentId::new)
        .collect(Collectors.toList());
    // 복수의 Document 저장
    documentIds
        .forEach(documentId ->
            bookmarkRepository.save(Bookmark.newBookmark(bookmarkRepository, accountId, documentId)));
    // findAllByOwnerId 로 저장된 Document 조회 후 검사
    List<Bookmark> savedBookmarks = bookmarkRepository.findAllByOwnerId(accountId)
        .collect(Collectors.toList());
    savedBookmarks
        .forEach(bookmark -> assertTrue(documentIds.contains(bookmark.getDocumentId())));
    // 저장된 Document 개수 확인
    assertEquals(num, savedBookmarks.size());
    assertEquals(num, bookmarkRepository.countByOwnerId(accountId));
    // 삭제 후 확인
    savedBookmarks
        .forEach(bookmarkRepository::delete);
    assertTrue(
        CollectionUtils.isEmpty(
            bookmarkRepository.findAllByOwnerId(accountId).collect(Collectors.toList())));
  }

  @Test
  @DisplayName("Bookmark 순차적으로 저장 후 countByOwnerId 값 확인")
  public void saveAndCountByOwnerId() {
    AccountId accountId = new AccountId(OWNER_ID);
    // 저장 전 countByOwnerId
    assertEquals(0L, bookmarkRepository.countByOwnerId(accountId));
    // 1개 저장
    DocumentId documentId1 = new DocumentId(DOCUMENT_ID + "1");
    Bookmark bookmark1 = Bookmark.newBookmark(bookmarkRepository, accountId, documentId1);
    bookmarkRepository.save(bookmark1);
    // 1개 저장 후 countByOwnerId 확인
    assertEquals(1L, bookmarkRepository.countByOwnerId(accountId));
    // 1개 추가
    DocumentId documentId2 = new DocumentId(DOCUMENT_ID + "2");
    Bookmark bookmark2 = Bookmark.newBookmark(bookmarkRepository, accountId, documentId2);
    bookmarkRepository.save(bookmark2);
    // 2개 저장 후 countByOwnerId 확인
    assertEquals(2L, bookmarkRepository.countByOwnerId(accountId));
    // 삭제 후 countByOwnerId 확인
    bookmarkRepository.delete(bookmark1);
    bookmarkRepository.delete(bookmark2);
    assertEquals(0L, bookmarkRepository.countByOwnerId(accountId));
  }

  @Test
  @DisplayName("Bookmark 저장 전후로 existsByDocumentId 호출")
  public void existsByDocumentId() {
    AccountId accountId = new AccountId(OWNER_ID);
    DocumentId documentId = new DocumentId(DOCUMENT_ID);
    // 저장 전 existsByDocumentId
    assertFalse(bookmarkRepository.existsByDocumentId(documentId));
    // Bookmark 저장
    Bookmark bookmark = Bookmark.newBookmark(bookmarkRepository, accountId, documentId);
    bookmarkRepository.save(bookmark);
    // 저장 후 existsByDocumentId
    assertTrue(bookmarkRepository.existsByDocumentId(documentId));
    // 삭제 후 existsByDocumentId
    bookmarkRepository.delete(bookmark);
    assertFalse(bookmarkRepository.existsByDocumentId(documentId));
  }
}
