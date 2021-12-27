package com.closememo.command.infra.persistence;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.category.CategoryId;
import com.closememo.command.domain.document.Document;
import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.domain.document.DocumentRepository;
import com.closememo.command.infra.persistence.imports.DocumentJpaRepository;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentRepositoryImpl implements DocumentRepository {

  private final SequenceGenerator sequenceGenerator;
  private final DocumentJpaRepository documentJpaRepository;

  public DocumentRepositoryImpl(
      SequenceGenerator sequenceGenerator,
      DocumentJpaRepository documentJpaRepository) {
    this.sequenceGenerator = sequenceGenerator;
    this.documentJpaRepository = documentJpaRepository;
  }

  @Override
  public DocumentId nextId() {
    return new DocumentId(sequenceGenerator.generate());
  }

  @Override
  public Document save(Document document) {
    return documentJpaRepository.save(document);
  }

  @Override
  public long countByOwnerId(AccountId accountId) {
    return documentJpaRepository.countByOwnerId(accountId);
  }

  @Override
  public Optional<Document> findById(DocumentId documentId) {
    return documentJpaRepository.findById(documentId);
  }

  @Override
  public Stream<Document> findAllByOwnerId(AccountId accountId) {
    return documentJpaRepository.findAllByOwnerId(accountId);
  }

  @Override
  public Stream<Document> findAllByIdIn(Iterable<DocumentId> documentIds) {
    return documentJpaRepository.findAllByIdIn(documentIds);
  }

  @Override
  public Stream<Document> findAllByCategoryId(CategoryId categoryId) {
    return documentJpaRepository.findAllByCategoryId(categoryId);
  }

  @Override
  public void delete(Document document) {
    documentJpaRepository.delete(document);
  }
}
