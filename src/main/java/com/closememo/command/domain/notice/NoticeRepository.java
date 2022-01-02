package com.closememo.command.domain.notice;

import java.util.Optional;

public interface NoticeRepository {

  NoticeId nextId();

  Notice save(Notice notice);

  Optional<Notice> findById(NoticeId noticeId);

  void delete(Notice notice);
}
