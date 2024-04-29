package com.closememo.command.domain.document;

import com.closememo.command.domain.Identifier;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class DocumentId implements Identifier {

  private static final long serialVersionUID = -43115001500834108L;

  private String id;

  public DocumentId(String id) {
    if (StringUtils.isBlank(id)) {
      throw new IllegalArgumentException("DocumentId must not be null or empty string.");
    }
    this.id = id;
  }
}
