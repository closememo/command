package com.closememo.command.domain.document;

import com.closememo.command.domain.account.AccountId;
import java.util.Optional;
import java.util.stream.Stream;

public interface DocumentRepository {

  DocumentId nextId();

  Document save(Document document);

  long countByOwnerId(AccountId accountId);

  Optional<Document> findById(DocumentId documentId);

  Stream<Document> findAllByOwnerId(AccountId accountId);

  Stream<Document> findAllByIdIn(Iterable<DocumentId> documentIds);

  void delete(Document document);
}
