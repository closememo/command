package com.closememo.command.infra.persistence;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.bookmark.Bookmark;
import com.closememo.command.domain.bookmark.BookmarkId;
import com.closememo.command.domain.bookmark.BookmarkRepository;
import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.infra.persistence.imports.BookmarkJpaRepository;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.stereotype.Repository;

@Repository
public class BookmarkRepositoryImpl implements BookmarkRepository {

  private final SequenceGenerator sequenceGenerator;
  private final BookmarkJpaRepository bookmarkJpaRepository;

  public BookmarkRepositoryImpl(
      SequenceGenerator sequenceGenerator,
      BookmarkJpaRepository bookmarkJpaRepository) {
    this.sequenceGenerator = sequenceGenerator;
    this.bookmarkJpaRepository = bookmarkJpaRepository;
  }

  @Override
  public BookmarkId nextId() {
    return new BookmarkId(sequenceGenerator.generate());
  }

  @Override
  public Bookmark save(Bookmark bookmark) {
    return bookmarkJpaRepository.save(bookmark);
  }

  @Override
  public Optional<Bookmark> findByDocumentId(DocumentId documentId) {
    return bookmarkJpaRepository.findByDocumentId(documentId);
  }

  @Override
  public Stream<Bookmark> findAllByOwnerId(AccountId ownerId) {
    return bookmarkJpaRepository.findAllByOwnerIdOrderByBookmarkOrder(ownerId);
  }

  @Override
  public long countByOwnerId(AccountId ownerId) {
    return bookmarkJpaRepository.countByOwnerId(ownerId);
  }

  @Override
  public boolean existsByDocumentId(DocumentId documentId) {
    return bookmarkJpaRepository.existsByDocumentId(documentId);
  }

  @Override
  public void delete(Bookmark bookmark) {
    bookmarkJpaRepository.delete(bookmark);
  }
}
