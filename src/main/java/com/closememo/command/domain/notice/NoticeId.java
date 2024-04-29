package com.closememo.command.domain.notice;

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
public class NoticeId implements Identifier {

  private static final long serialVersionUID = -7670144163326330261L;

  private String id;

  public NoticeId(String id) {
    if (StringUtils.isBlank(id)) {
      throw new IllegalArgumentException("NoticeId must not be null or empty string.");
    }
    this.id = id;
  }
}
