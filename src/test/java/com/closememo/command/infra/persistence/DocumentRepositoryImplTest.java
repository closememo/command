package com.closememo.command.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.category.CategoryId;
import com.closememo.command.domain.document.Document;
import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.domain.document.DocumentOption;
import com.closememo.command.domain.document.DocumentRepository;
import com.closememo.command.infra.persistence.imports.DocumentJpaRepository;
import com.closememo.command.test.ImportSequenceGenerator;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ImportSequenceGenerator
@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class DocumentRepositoryImplTest {

  private static final String OWNER_ID = "ownerId";
  private static final String CATEGORY_ID = "categoryId";

  @Autowired
  private DocumentJpaRepository documentJpaRepository;
  @Autowired
  private SequenceGenerator sequenceGenerator;
  private DocumentRepository documentRepository;

  @BeforeEach
  public void beforeEach() {
    documentRepository = new DocumentRepositoryImpl(sequenceGenerator, documentJpaRepository);
  }

  @AfterEach
  public void afterEach() {
    documentJpaRepository.deleteAll();
  }

  @Test
  @DisplayName("DocumentId 생성")
  public void createDocumentId() {
    DocumentId documentId = documentRepository.nextId();
    assertNotNull(documentId);
  }

  @Test
  @DisplayName("순차적으로 Document 저장하며 countByOwnerId")
  public void saveDocumentsAndCountByOwnerId() {
    AccountId accountId = new AccountId(OWNER_ID);
    CategoryId categoryId = new CategoryId(CATEGORY_ID);
    // 저장 전 countByOwnerId
    assertEquals(0L, documentRepository.countByOwnerId(accountId));
    // Document 하나 저장 후 countByOwnerId
    Document document1 = Document.newOne(documentRepository, accountId, categoryId,
        "title", "content", Collections.emptyList(), DocumentOption.newOne());
    documentRepository.save(document1);
    assertEquals(1L, documentRepository.countByOwnerId(accountId));
    // Document 하나 저장 후 countByOwnerId
    Document document2 = Document.newOne(documentRepository, accountId, categoryId,
        "title", "content", Collections.emptyList(), DocumentOption.newOne());
    documentRepository.save(document2);
    assertEquals(2L, documentRepository.countByOwnerId(accountId));
    // Document 하나 저장 후 countByOwnerId
    Document document3 = Document.newOne(documentRepository, accountId, categoryId,
        "title", "content", Collections.emptyList(), DocumentOption.newOne());
    documentRepository.save(document3);
    assertEquals(3L, documentRepository.countByOwnerId(accountId));
    // 삭제 후 countByOwnerId
    documentRepository.delete(document1);
    documentRepository.delete(document2);
    documentRepository.delete(document3);
    assertEquals(0L, documentRepository.countByOwnerId(accountId));
  }

  @Test
  @DisplayName("다수의 Document 저장 후 DocumentId 목록으로 특정 Document 목록 조회 (findAllByIdIn)")
  public void saveDocumentsAndFindAllByIdIn() {
    AccountId accountId = new AccountId(OWNER_ID);
    CategoryId categoryId = new CategoryId(CATEGORY_ID);
    // 다수의 Document 저장
    Document document1 = Document.newOne(documentRepository, accountId, categoryId,
        "title", "content", Collections.emptyList(), DocumentOption.newOne());
    documentRepository.save(document1);
    Document document2 = Document.newOne(documentRepository, accountId, categoryId,
        "title", "content", Collections.emptyList(), DocumentOption.newOne());
    documentRepository.save(document2);
    Document document3 = Document.newOne(documentRepository, accountId, categoryId,
        "title", "content", Collections.emptyList(), DocumentOption.newOne());
    documentRepository.save(document3);
    // document1, document2 두 개를 조회
    List<DocumentId> target = Arrays.asList(document1.getId(), document2.getId());
    List<Document> documents = documentRepository.findAllByIdIn(target)
        .collect(Collectors.toList());
    // 조회한 Document 검증
    List<DocumentId> documentIds = documents.stream()
        .map(Document::getId)
        .collect(Collectors.toList());
    assertEquals(2, documentIds.size());
    assertTrue(documentIds.contains(document1.getId()));
    assertTrue(documentIds.contains(document2.getId()));
    assertFalse(documentIds.contains(document3.getId())); // document3 은 조회되지 않음
    // 삭제
    documentRepository.delete(document1);
    documentRepository.delete(document2);
    documentRepository.delete(document3);
  }

  @Test
  @DisplayName("다수의 Document 저장 후 CategoryId 로 조회 (findAllByCategoryId)")
  public void cc() {
    int num = 10;

    AccountId accountId = new AccountId(OWNER_ID);
    CategoryId categoryId = new CategoryId(CATEGORY_ID);
    // 복수의 Document 저장
    List<Document> documents = Stream.generate(() ->
            documentRepository.save(Document.newOne(documentRepository, accountId, categoryId,
                "title", "content", Collections.emptyList(), DocumentOption.newOne())))
        .limit(num)
        .collect(Collectors.toList());
    // 저장한 Document 의 DocumentId 목록
    List<DocumentId> documentIds = documents.stream()
        .map(Document::getId)
        .collect(Collectors.toList());
    // findAllByCategoryId 로 저장된 Document 목록 조회
    List<Document> savedDocuments = documentRepository.findAllByCategoryId(categoryId)
        .collect(Collectors.toList());
    // 저장된 Document 검증
    assertEquals(num, savedDocuments.size());
    savedDocuments.forEach(document -> assertTrue(documentIds.contains(document.getId())));
    // 삭제 후 확인
    savedDocuments.forEach(documentRepository::delete);
    assertEquals(0L, documentRepository.findAllByCategoryId(categoryId).count());
  }
}
