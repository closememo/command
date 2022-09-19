package com.closememo.command.domain.document;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DocumentIdTest {

  @Test
  @DisplayName("EqualsAndHashCode 테스트")
  public void testEqualsAndHashCode() {
    DocumentId documentId1 = new DocumentId("documentId");
    DocumentId documentId2 = new DocumentId("documentId");

    assertEquals(documentId1, documentId2);
  }
}
