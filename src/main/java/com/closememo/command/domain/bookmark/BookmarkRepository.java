package com.closememo.command.domain.bookmark;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.document.DocumentId;
import java.util.Optional;
import java.util.stream.Stream;

public interface BookmarkRepository {

  BookmarkId nextId();

  Bookmark save(Bookmark bookmark);

  Optional<Bookmark> findByDocumentId(DocumentId documentId);

  Stream<Bookmark> findAllByOwnerId(AccountId ownerId);

  long countByOwnerId(AccountId ownerId);

  boolean existsByDocumentId(DocumentId documentId);

  void delete(Bookmark bookmark);
}
