package com.closememo.command.infra.persistence;

import com.closememo.command.domain.difference.Difference;
import com.closememo.command.domain.difference.DifferenceId;
import com.closememo.command.domain.difference.DifferenceRepository;
import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.infra.persistence.imports.DifferenceJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class DifferenceRepositoryImpl implements DifferenceRepository {

  private final DifferenceJpaRepository differenceJpaRepository;
  private final SequenceGenerator sequenceGenerator;

  public DifferenceRepositoryImpl(DifferenceJpaRepository differenceJpaRepository,
      SequenceGenerator sequenceGenerator) {
    this.differenceJpaRepository = differenceJpaRepository;
    this.sequenceGenerator = sequenceGenerator;
  }

  @Override
  public DifferenceId nextId() {
    return new DifferenceId(sequenceGenerator.generate());
  }

  @Override
  public Difference save(Difference difference) {
    return differenceJpaRepository.save(difference);
  }

  @Override
  public long countByDocumentId(DocumentId documentId) {
    return differenceJpaRepository.countByDocumentId(documentId);
  }

  @Override
  public Optional<Difference> findById(DifferenceId differenceId) {
    return differenceJpaRepository.findById(differenceId);
  }

  @Override
  public List<Difference> findAllByDocumentId(DocumentId documentId) {
    return differenceJpaRepository.findAllByDocumentId(documentId);
  }

  @Override
  public void delete(Difference difference) {
    differenceJpaRepository.delete(difference);
  }
}
