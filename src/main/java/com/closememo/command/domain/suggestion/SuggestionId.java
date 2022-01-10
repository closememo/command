package com.closememo.command.domain.suggestion;

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
public class SuggestionId implements Identifier {

  private static final long serialVersionUID = 945604463436040909L;

  private String id;

  public SuggestionId(String id) {
    if (StringUtils.isBlank(id)) {
      throw new IllegalArgumentException("SuggestionId must not be null or empty string.");
    }
    this.id = id;
  }
}
