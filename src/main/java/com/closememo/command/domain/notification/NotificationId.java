package com.closememo.command.domain.notification;

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
public class NotificationId implements Identifier {

  private static final long serialVersionUID = -2931665221082921906L;

  private String id;

  public NotificationId(String id) {
    if (StringUtils.isBlank(id)) {
      throw new IllegalArgumentException("NotificationId must not be null or empty string.");
    }
    this.id = id;
  }
}
