package com.closememo.command.infra.persistence.imports;

import com.closememo.command.domain.notice.Notice;
import com.closememo.command.domain.notice.NoticeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeJpaRepository extends JpaRepository<Notice, NoticeId> {

}
