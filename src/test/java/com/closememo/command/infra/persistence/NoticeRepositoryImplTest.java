package com.closememo.command.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.closememo.command.domain.notice.Notice;
import com.closememo.command.domain.notice.NoticeId;
import com.closememo.command.domain.notice.NoticeRepository;
import com.closememo.command.infra.persistence.imports.NoticeJpaRepository;
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
class NoticeRepositoryImplTest {

  @Autowired
  private NoticeJpaRepository noticeJpaRepository;
  @Autowired
  private SequenceGenerator sequenceGenerator;
  private NoticeRepository noticeRepository;

  @BeforeEach
  public void beforeEach() {
    noticeRepository = new NoticeRepositoryImpl(sequenceGenerator, noticeJpaRepository);
  }

  @AfterEach
  public void afterEach() {
    noticeJpaRepository.deleteAll();
  }

  @Test
  @DisplayName("NoticeId 생성")
  public void createNoticeId() {
    NoticeId noticeId = noticeRepository.nextId();
    assertNotNull(noticeId);
  }

  @Test
  @DisplayName("Notice 저장 후 조회 및 삭제")
  public void saveNoticeAndFindById() {
    // 저장
    Notice notice = Notice.newOne(noticeRepository.nextId(), "title", "content");
    noticeRepository.save(notice);
    // 조회
    Optional<Notice> optionalNotice = noticeRepository.findById(notice.getId());
    assertTrue(optionalNotice.isPresent());

    Notice saved = optionalNotice.get();
    assertEquals(notice.getId(), saved.getId());
    // 삭제 후 확인
    noticeRepository.delete(saved);
    assertTrue(noticeRepository.findById(notice.getId()).isEmpty());
  }
}
