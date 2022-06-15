package com.closememo.command.application.difference;

import com.closememo.command.application.Success;
import com.closememo.command.domain.difference.Difference;
import com.closememo.command.domain.difference.DifferenceId;
import com.closememo.command.domain.difference.DifferenceNotFoundException;
import com.closememo.command.domain.difference.DifferenceRepository;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DifferenceCommandHandler {

  private final DifferenceRepository differenceRepository;

  public DifferenceCommandHandler(
      DifferenceRepository differenceRepository) {
    this.differenceRepository = differenceRepository;
  }

  @ServiceActivator(inputChannel = "CreateDifferenceCommand")
  @Transactional
  public DifferenceId handle(CreateDifferenceCommand command) {
    Difference difference = Difference.newOne(differenceRepository, command.getOwnerId(),
        command.getDocumentId(), command.getDocumentVersion(), command.getLineDeltas());

    Difference savedDifference = differenceRepository.save(difference);

    return savedDifference.getId();
  }

  @ServiceActivator(inputChannel = "DeleteDifferenceCommand")
  @Transactional
  public Success handle(DeleteDifferenceCommand command) {
    Difference difference = differenceRepository.findById(command.getDifferenceId())
        .orElseThrow(DifferenceNotFoundException::new);

    difference.delete();
    differenceRepository.delete(difference);
    return Success.getInstance();
  }
}
