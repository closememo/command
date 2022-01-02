package com.closememo.command.application.notice;

import com.closememo.command.application.Success;
import com.closememo.command.domain.notice.Notice;
import com.closememo.command.domain.notice.NoticeId;
import com.closememo.command.domain.notice.NoticeNotFoundException;
import com.closememo.command.domain.notice.NoticeRepository;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoticeCommandHandler {

  private final NoticeRepository noticeRepository;

  public NoticeCommandHandler(NoticeRepository noticeRepository) {
    this.noticeRepository = noticeRepository;
  }

  @Transactional
  @ServiceActivator(inputChannel = "CreateNoticeCommand")
  public NoticeId handle(CreateNoticeCommand command) {
    NoticeId noticeId = noticeRepository.nextId();

    Notice notice = Notice.newOne(noticeId, command.getTitle(), command.getContent());

    Notice savedNotice = noticeRepository.save(notice);

    return savedNotice.getId();
  }

  @Transactional
  @ServiceActivator(inputChannel = "UpdateNoticeCommand")
  public NoticeId handle(UpdateNoticeCommand command) {
    Notice notice = noticeRepository.findById(command.getNoticeId())
        .orElseThrow(NoticeNotFoundException::new);

    notice.update(command.getTitle(), command.getContent());
    Notice savedNotice = noticeRepository.save(notice);

    return savedNotice.getId();
  }

  @Transactional
  @ServiceActivator(inputChannel = "DeleteNoticeCommand")
  public Success handle(DeleteNoticeCommand command) {
    Notice notice = noticeRepository.findById(command.getNoticeId())
        .orElseThrow(NoticeNotFoundException::new);

    notice.delete();
    noticeRepository.delete(notice);

    return Success.getInstance();
  }
}
