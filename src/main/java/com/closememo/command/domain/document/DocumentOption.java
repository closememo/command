package com.closememo.command.domain.document;

import com.closememo.command.domain.ValueObject;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentOption implements ValueObject {

  private static final long serialVersionUID = 2353982346323389360L;

  @EqualsAndHashCode.Include
  private boolean hasAutoTag;

  public DocumentOption(boolean hasAutoTag) {
    this.hasAutoTag = hasAutoTag;
  }

  public static DocumentOption newOne() {
    return new DocumentOption(true);
  }
}
