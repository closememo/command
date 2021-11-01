package com.closememo.command.infra.sequencegenerator;

import com.closememo.command.infra.persistence.SequenceGenerator;
import org.springframework.stereotype.Component;

@Component
public class SequenceGeneratorImpl implements SequenceGenerator {

  @Override
  public String generate() {
    return new ObjectId().toHexString();
  }
}
