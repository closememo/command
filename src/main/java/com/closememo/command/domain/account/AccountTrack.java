package com.closememo.command.domain.account;

import com.closememo.command.domain.ValueObject;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountTrack implements ValueObject {

  private static final long serialVersionUID = -2871027337720563606L;

  @Column(columnDefinition = "VARCHAR(24)")
  @EqualsAndHashCode.Include
  private String recentlyViewedCategoryId;

  public AccountTrack(String recentlyViewedCategoryId) {
    this.recentlyViewedCategoryId = recentlyViewedCategoryId;
  }

  public static AccountTrack emptyOne() {
    return new AccountTrack(null);
  }
}
