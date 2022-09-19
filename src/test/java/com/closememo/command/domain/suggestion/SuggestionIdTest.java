package com.closememo.command.domain.suggestion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SuggestionIdTest {

  @Test
  @DisplayName("EqualsAndHashCode 테스트")
  public void testEqualsAndHashCode() {
    SuggestionId suggestionId1 = new SuggestionId("suggestionId");
    SuggestionId suggestionId2 = new SuggestionId("suggestionId");

    assertEquals(suggestionId1, suggestionId2);
  }
}
