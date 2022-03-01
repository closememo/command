package com.closememo.command.domain.bookmark;

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
public class BookmarkId implements Identifier {

  private static final long serialVersionUID = -3468030091013833403L;

  private String id;

  public BookmarkId(String id) {
    if (StringUtils.isBlank(id)) {
      throw new IllegalArgumentException("CategoryId must not be null or empty string.");
    }
    this.id = id;
  }
}
