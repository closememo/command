package com.closememo.command.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.closememo.command.test.ImportSequenceGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ImportSequenceGenerator
class SequenceGeneratorTest {

  @Autowired
  private SequenceGenerator sequenceGenerator;

  @Test
  public void generateNewId() {
    String id1 = sequenceGenerator.generate();
    String id2 = sequenceGenerator.generate();

    assertNotNull(id1);
    assertNotNull(id2);
    assertNotEquals(id1, id2);
  }
}
