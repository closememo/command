package com.closememo.command.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.suggestion.Status;
import com.closememo.command.domain.suggestion.Suggestion;
import com.closememo.command.domain.suggestion.SuggestionRepository;
import com.closememo.command.infra.persistence.imports.SuggestionJpaRepository;
import com.closememo.command.test.ImportSequenceGenerator;
import java.util.Optional;
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
class SuggestionRepositoryImplTest {

  private static final String OWNER_ID = "ownerId";

  @Autowired
  private SuggestionJpaRepository suggestionJpaRepository;
  @Autowired
  private SequenceGenerator sequenceGenerator;
  private SuggestionRepository suggestionRepository;

  @BeforeEach
  public void beforeEach() {
    suggestionRepository = new SuggestionRepositoryImpl(sequenceGenerator, suggestionJpaRepository);
  }

  @AfterEach
  public void afterEach() {
    suggestionJpaRepository.deleteAll();
  }

  @Test
  @DisplayName("Suggestion 저장 후 조회 및 삭제")
  public void saveSuggestionAndFindById() {
    // 저장
    AccountId accountId = new AccountId(OWNER_ID);
    Suggestion suggestion = Suggestion.newOne(suggestionRepository, accountId, "content");
    suggestionRepository.save(suggestion);
    // 조회
    Optional<Suggestion> optionalSuggestion = suggestionRepository.findById(suggestion.getId());
    assertTrue(optionalSuggestion.isPresent());

    Suggestion saved = optionalSuggestion.get();
    assertEquals(suggestion.getId(), saved.getId());
    // 삭제 후 확인
    suggestionRepository.delete(suggestion);
    assertTrue(suggestionRepository.findById(suggestion.getId()).isEmpty());
  }

  @Test
  @DisplayName("Suggestion 저장하거나 상태를 변경하면서, 특정 Status 가 아니고 AccountId 가 동일한 것의 개수를 구함")
  public void saveSuggestionAndCountByWriterIdAndStatusNot() {
    AccountId accountId = new AccountId(OWNER_ID);
    // 저장 전, DELETED 상태가 아니고 accountId 를 가진 것의 개수를 구함
    assertEquals(0L, suggestionRepository.countByWriterIdAndStatusNot(accountId, Status.DELETED));
    // Suggestion 저장 후, DELETED 상태가 아니고 accountId 를 가진 것의 개수를 구함
    Suggestion suggestion1 = Suggestion.newOne(suggestionRepository, accountId, "content");
    suggestionRepository.save(suggestion1);
    assertEquals(1L, suggestionRepository.countByWriterIdAndStatusNot(accountId, Status.DELETED));
    // Suggestion 저장 후, DELETED 상태가 아니고 accountId 를 가진 것의 개수를 구함
    Suggestion suggestion2 = Suggestion.newOne(suggestionRepository, accountId, "content");
    suggestionRepository.save(suggestion2);
    assertEquals(2L, suggestionRepository.countByWriterIdAndStatusNot(accountId, Status.DELETED));
    // suggestion1 의 상태를 DELETED 로 수정 및 저장하고, 개수를 구함
    suggestion1.setDeletedStatus();
    suggestionRepository.save(suggestion1);
    assertEquals(1L, suggestionRepository.countByWriterIdAndStatusNot(accountId, Status.DELETED));
    // 삭제
    suggestionRepository.delete(suggestion1);
    suggestionRepository.delete(suggestion2);
  }
}
