package com.closememo.command.domain.difference;

import com.closememo.command.domain.document.DocumentId;
import java.util.List;
import java.util.Optional;

public interface DifferenceRepository {

  DifferenceId nextId();

  Difference save(Difference difference);

  Optional<Difference> findById(DifferenceId differenceId);

  List<Difference> findAllByDocumentId(DocumentId documentId);

  void delete(Difference difference);
}
