package com.closememo.command.infra.persistence.imports;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.category.CategoryId;
import com.closememo.command.domain.document.Document;
import com.closememo.command.domain.document.DocumentId;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentJpaRepository extends JpaRepository<Document, DocumentId> {

  long countByOwnerId(AccountId accountId);

  Stream<Document> findAllByIdIn(Iterable<DocumentId> documentIds);

  Stream<Document> findAllByCategoryId(CategoryId categoryId);
}
