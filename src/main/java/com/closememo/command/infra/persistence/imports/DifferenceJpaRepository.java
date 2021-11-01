package com.closememo.command.infra.persistence.imports;

import com.closememo.command.domain.difference.Difference;
import com.closememo.command.domain.difference.DifferenceId;
import com.closememo.command.domain.document.DocumentId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DifferenceJpaRepository extends JpaRepository<Difference, DifferenceId> {

  List<Difference> findAllByDocumentId(DocumentId documentId);
}
