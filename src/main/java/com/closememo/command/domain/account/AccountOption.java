package com.closememo.command.domain.account;

import com.closememo.command.domain.ValueObject;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountOption implements ValueObject {

  private static final long serialVersionUID = 1818887309106632685L;

  @Column(columnDefinition = "VARCHAR(20)")
  @Enumerated(EnumType.STRING)
  @EqualsAndHashCode.Include
  private DocumentOrderType documentOrderType;
  @EqualsAndHashCode.Include
  private int documentCount;

  @Builder(toBuilder = true)
  public AccountOption(DocumentOrderType documentOrderType, int documentCount) {
    this.documentOrderType = documentOrderType;
    this.documentCount = documentCount;
  }

  public static AccountOption newOne() {
    return new AccountOption(DocumentOrderType.CREATED_NEWEST, 10);
  }

  public enum DocumentOrderType {
    CREATED_NEWEST,
    CREATED_OLDEST,
    UPDATED_NEWEST,
  }
}
