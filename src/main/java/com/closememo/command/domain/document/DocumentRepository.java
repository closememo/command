package com.closememo.command.domain.document;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.category.CategoryId;
import java.util.Optional;
import java.util.stream.Stream;

public interface DocumentRepository {

  DocumentId nextId();

  Document save(Document document);

  long countByOwnerId(AccountId accountId);

  Optional<Document> findById(DocumentId documentId);

  Stream<Document> findAllByIdIn(Iterable<DocumentId> documentIds);

  Stream<Document> findAllByCategoryId(CategoryId categoryId);

  void delete(Document document);
}
