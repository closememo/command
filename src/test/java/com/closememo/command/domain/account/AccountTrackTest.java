package com.closememo.command.domain.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AccountTrackTest {

  @Test
  @DisplayName("빈 AccountTrack 생성")
  public void createEmptyAccountTrack() {
    AccountTrack accountTrack = AccountTrack.emptyOne();

    assertNull(accountTrack.getRecentlyViewedCategoryId());
  }

  @Test
  @DisplayName("EqualsAndHashCode 테스트")
  public void testEqualsAndHashCode() {
    AccountTrack accountTrack1 = new AccountTrack("categoryId");
    AccountTrack accountTrack2 = new AccountTrack("categoryId");

    assertEquals(accountTrack1, accountTrack2);
  }
}
