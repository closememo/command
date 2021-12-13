package com.closememo.command.domain.category;

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
public class CategoryId implements Identifier {

  private static final long serialVersionUID = -2200544413801517789L;

  private String id;

  public CategoryId(String id) {
    if (StringUtils.isBlank(id)) {
      throw new IllegalArgumentException("CategoryId must not be null or empty string.");
    }
    this.id = id;
  }
}
