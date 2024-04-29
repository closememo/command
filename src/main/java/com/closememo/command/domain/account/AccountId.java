package com.closememo.command.domain.account;

import com.closememo.command.domain.Identifier;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Embeddable
@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountId implements Identifier {

  private static final long serialVersionUID = 485759680788572242L;

  private String id;

  public AccountId(String id) {
    if (StringUtils.isBlank(id)) {
      throw new IllegalArgumentException("AccountId must not be null or empty string.");
    }
    this.id = id;
  }
}
