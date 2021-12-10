package com.closememo.command.domain.document;

import com.closememo.command.domain.ValueObject;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentOption implements ValueObject {

  private static final long serialVersionUID = 2353982346323389360L;

  private boolean hasAutoTag;

  public DocumentOption(boolean hasAutoTag) {
    this.hasAutoTag = hasAutoTag;
  }
}
