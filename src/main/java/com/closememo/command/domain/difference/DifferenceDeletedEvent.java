package com.closememo.command.domain.difference;

import com.closememo.command.domain.DomainEvent;
import lombok.Getter;

@Getter
public class DifferenceDeletedEvent extends DomainEvent {

  private static final long serialVersionUID = 1162707853428942971L;

  private final DifferenceId differenceId;

  public DifferenceDeletedEvent(DifferenceId differenceId) {
    super(differenceId.getId(), 1);
    this.differenceId = differenceId;
  }
}
