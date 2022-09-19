package com.closememo.command.domain.difference;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DifferenceIdTest {

  @Test
  @DisplayName("EqualsAndHashCode 테스트")
  public void testEqualsAndHashCode() {
    DifferenceId differenceId1 = new DifferenceId("differenceId");
    DifferenceId differenceId2 = new DifferenceId("differenceId");

    assertEquals(differenceId1, differenceId2);
  }
}
