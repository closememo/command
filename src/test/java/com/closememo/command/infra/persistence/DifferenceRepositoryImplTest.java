package com.closememo.command.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.difference.Difference;
import com.closememo.command.domain.difference.DifferenceId;
import com.closememo.command.domain.difference.DifferenceRepository;
import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.infra.persistence.imports.DifferenceJpaRepository;
import com.closememo.command.test.ImportSequenceGenerator;
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
class DifferenceRepositoryImplTest {

  private static final String OWNER_ID = "ownerId";
  private static final String DOCUMENT_ID = "documentId";

  @Autowired
  private DifferenceJpaRepository differenceJpaRepository;
  @Autowired
  private SequenceGenerator sequenceGenerator;
  private DifferenceRepository differenceRepository;

  @BeforeEach
  public void beforeEach() {
    differenceRepository = new DifferenceRepositoryImpl(differenceJpaRepository, sequenceGenerator);
  }

  @AfterEach
  public void afterEach() {
    differenceJpaRepository.deleteAll();
  }

  @Test
  @DisplayName("DifferenceId 생성")
  public void createDifferenceId() {
    DifferenceId differenceId = differenceRepository.nextId();
    assertNotNull(differenceId);
  }

  @Test
  @DisplayName("순차적으로 Difference 저장하며 countByDocumentId")
  public void saveDifferencesAndCountByDocumentId() {
    AccountId accountId = new AccountId(OWNER_ID);
    DocumentId documentId = new DocumentId(DOCUMENT_ID);
    // 저장 전 countByDocumentId
    assertEquals(0L, differenceRepository.countByDocumentId(documentId));
    // Difference 하나 저장 후 countByDocumentId
    Difference difference1 = Difference.newOne(differenceRepository, accountId, documentId,
        1L, Collections.emptyList());
    differenceRepository.save(difference1);
    assertEquals(1L, differenceRepository.countByDocumentId(documentId));
    // Difference 하나 저장 후 countByDocumentId
    Difference difference2 = Difference.newOne(differenceRepository, accountId, documentId,
        1L, Collections.emptyList());
    differenceRepository.save(difference2);
    assertEquals(2L, differenceRepository.countByDocumentId(documentId));
    // 삭제 후 countByDocumentId
    differenceRepository.delete(difference1);
    differenceRepository.delete(difference2);
    assertEquals(0L, differenceRepository.countByDocumentId(documentId));
  }

  @Test
  @DisplayName("다수의 Difference 저장 후 DocumentId 로 findAllByDocumentId")
  public void saveDifferencesAndFindAllByDocumentId() {
    int num = 10;

    AccountId accountId = new AccountId(OWNER_ID);
    DocumentId documentId = new DocumentId(DOCUMENT_ID);
    // 복수의 Difference 저장
    List<Difference> differences = Stream.generate(() ->
            differenceRepository.save(Difference.newOne(differenceRepository, accountId,
                documentId, 1L, Collections.emptyList())))
        .limit(num)
        .collect(Collectors.toList());
    // 저장한 Difference 의 DifferenceId 목록
    List<DifferenceId> differenceIds = differences.stream()
        .map(Difference::getId)
        .collect(Collectors.toList());
    // findAllByDocumentId 로 저장된 Difference 목록 조회
    List<Difference> savedDifferences = differenceRepository.findAllByDocumentId(documentId);
    // 저장된 Difference 검증
    assertEquals(num, savedDifferences.size());
    savedDifferences.forEach(difference -> assertTrue(differenceIds.contains(difference.getId())));
    // 삭제 후 확인
    savedDifferences.forEach(differenceRepository::delete);
    assertEquals(0L, differenceRepository.findAllByDocumentId(documentId).size());
  }
}
