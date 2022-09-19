package com.closememo.command.domain.category;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CategoryIdTest {

  @Test
  @DisplayName("EqualsAndHashCode 테스트")
  public void testEqualsAndHashCode() {
    CategoryId categoryId1 = new CategoryId("categoryId");
    CategoryId categoryId2 = new CategoryId("categoryId");

    assertEquals(categoryId1, categoryId2);
  }
}