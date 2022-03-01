package com.closememo.command.infra.persistence.imports;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.bookmark.Bookmark;
import com.closememo.command.domain.bookmark.BookmarkId;
import com.closememo.command.domain.document.DocumentId;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkJpaRepository extends JpaRepository<Bookmark, BookmarkId> {

  Optional<Bookmark> findByDocumentId(DocumentId documentId);

  Stream<Bookmark> findAllByOwnerIdOrderByBookmarkOrder(AccountId ownerId);

  long countByOwnerId(AccountId ownerId);

  boolean existsByDocumentId(DocumentId documentId);
}
