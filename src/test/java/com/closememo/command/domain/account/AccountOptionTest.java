package com.closememo.command.domain.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.closememo.command.domain.account.AccountOption.DocumentOrderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AccountOptionTest {

  @Test
  @DisplayName("AccountOption 생성")
  public void createAccountOption() {
    AccountOption accountOption = AccountOption.newOne();

    assertEquals(DocumentOrderType.CREATED_NEWEST, accountOption.getDocumentOrderType());
    assertEquals(10, accountOption.getDocumentCount());
  }

  @Test
  @DisplayName("EqualsAndHashCode 테스트")
  public void testEqualsAndHashCode() {
    AccountOption accountOption1 = new AccountOption(DocumentOrderType.CREATED_OLDEST, 10);
    AccountOption accountOption2 = new AccountOption(DocumentOrderType.CREATED_OLDEST, 10);

    assertEquals(accountOption1, accountOption2);
  }
}
