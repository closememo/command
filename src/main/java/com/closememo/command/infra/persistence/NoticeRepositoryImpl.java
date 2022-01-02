package com.closememo.command.infra.persistence;

import com.closememo.command.domain.notice.Notice;
import com.closememo.command.domain.notice.NoticeId;
import com.closememo.command.domain.notice.NoticeRepository;
import com.closememo.command.infra.persistence.imports.NoticeJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class NoticeRepositoryImpl implements NoticeRepository {

  private final SequenceGenerator sequenceGenerator;
  private final NoticeJpaRepository noticeJpaRepository;

  public NoticeRepositoryImpl(
      SequenceGenerator sequenceGenerator,
      NoticeJpaRepository noticeJpaRepository) {
    this.sequenceGenerator = sequenceGenerator;
    this.noticeJpaRepository = noticeJpaRepository;
  }

  @Override
  public NoticeId nextId() {
    return new NoticeId(sequenceGenerator.generate());
  }

  @Override
  public Notice save(Notice notice) {
    return noticeJpaRepository.save(notice);
  }

  @Override
  public Optional<Notice> findById(NoticeId noticeId) {
    return noticeJpaRepository.findById(noticeId);
  }

  @Override
  public void delete(Notice notice) {
    noticeJpaRepository.delete(notice);
  }
}
