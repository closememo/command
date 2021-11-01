package com.closememo.command.domain.difference;

import com.closememo.command.domain.Identifier;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class DifferenceId implements Identifier {

  private static final long serialVersionUID = 1733769450020552992L;

  private String id;

  public DifferenceId(String id) {
    if (StringUtils.isBlank(id)) {
      throw new IllegalArgumentException("DifferenceId must not be null or empty string.");
    }
    this.id = id;
  }
}
