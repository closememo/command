package com.closememo.command.domain.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AccountIdTest {

  @Test
  @DisplayName("EqualsAndHashCode 테스트")
  public void testEqualsAndHashCode() {
    AccountId accountId1 = new AccountId("accountId");
    AccountId accountId2 = new AccountId("accountId");

    assertEquals(accountId1, accountId2);
  }
}
