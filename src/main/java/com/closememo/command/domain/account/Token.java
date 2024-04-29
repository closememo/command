package com.closememo.command.domain.account;

import com.closememo.command.domain.ValueObject;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token implements ValueObject {

  private static final long serialVersionUID = -7537999338660895931L;

  private String tokenId;
  private long exp;
  private String childId;

  public Token(String tokenId, long exp) {
    this(tokenId, exp, null);
  }

  public Token(String tokenId, long exp, String childId) {
    this.tokenId = tokenId;
    this.exp = exp;
    this.childId = childId;
  }

  public void setChildId(String childId) {
    this.childId = childId;
  }
}
