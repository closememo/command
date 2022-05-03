package com.closememo.command.domain.account;

import com.closememo.command.domain.Events;
import com.closememo.command.domain.category.CategoryId;
import com.closememo.command.infra.persistence.converters.RoleCollectionConverter;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

@Entity
@Table(
    name = "accounts",
    indexes = {
        @Index(name = "idx_social_id", columnList = "socialId", unique = true)
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

  private static final int MIN_EMAIL_LENGTH = 3;
  private static final int MAX_EMAIL_LENGTH = 100;
  private static final int NUMBER_OF_TOKEN_LIMIT = 10;
  private static final int TOKEN_EXP_BUFFER_SECONDS = 1; // 종종 하나의 토큰으로 여러개의 요청이 올 수 있다. 바로 삭제하지 않고 버퍼를 둔다.
  private static final Pattern EMAIL_PATTERN = Pattern.compile("[0-9a-zA-Z-_.]+@[0-9a-zA-Z-]+");

  @EmbeddedId
  private AccountId id;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Social social;
  @Column(nullable = false)
  private String socialId;
  @Column(nullable = false, columnDefinition = "VARCHAR(100)")
  private String email;
  @ElementCollection
  @CollectionTable(
      name = "tokens",
      joinColumns = @JoinColumn(name = "account_id"),
      indexes = {
          @Index(name = "idx_token_id", columnList = "tokenId", unique = true)
      }
  )
  private List<Token> tokens;
  @Column(nullable = false, columnDefinition = "VARCHAR(100)")
  @Convert(converter = RoleCollectionConverter.class)
  private Set<Role> roles;
  @Embedded
  private AccountOption option;
  @Embedded
  private AccountTrack track;
  @Column(nullable = false)
  private ZonedDateTime createdAt;

  private Account(AccountId id, Social social, String socialId, String email, List<Token> tokens,
      Set<Role> roles, AccountOption option, AccountTrack track, ZonedDateTime createdAt) {
    this.id = id;
    this.social = social;
    this.socialId = socialId;
    this.email = email;
    this.tokens = tokens;
    this.roles = roles;
    this.option = option;
    this.track = track;
    this.createdAt = createdAt;
  }

  public static Account newOne(AccountRepository accountRepository, Social social,
      String socialId, String email, List<Token> tokens) {

    validateEmail(accountRepository, email);

    Set<Role> userRoleSet = Set.of(Role.USER);
    AccountOption option = AccountOption.newOne();
    AccountTrack track = AccountTrack.emptyOne();
    ZonedDateTime createdAt = ZonedDateTime.now();

    Account account = new Account(accountRepository.nextId(), social, socialId, email,
        tokens, userRoleSet, option, track, createdAt);
    Events.register(new AccountCreatedEvent(account.getId(), email, tokens, userRoleSet,
        option, track, createdAt).needAck());
    return account;
  }

  public static Account newTempOne(AccountId id, String ip, List<Token> tokens) {
    Set<Role> tempUserRoleSet = Set.of(Role.USER, Role.TEMP);
    AccountOption option = AccountOption.newOne();
    AccountTrack track = AccountTrack.emptyOne();
    ZonedDateTime createdAt = ZonedDateTime.now();

    Account account = new Account(id, Social.NONE, ip, StringUtils.EMPTY,
        tokens, tempUserRoleSet, option, track, createdAt);
    Events.register(new AccountCreatedEvent(account.getId(), StringUtils.EMPTY, tokens,
        tempUserRoleSet, option, track, createdAt).needAck());
    return account;
  }

  private static void validateEmail(AccountRepository accountRepository, String email) {
    if (!EMAIL_PATTERN.matcher(email).find()) {
      throw new InvalidEmailException("invalid email pattern");
    }
    if (email.length() < MIN_EMAIL_LENGTH) {
      throw new InvalidEmailException(
          String.format("displayId is shorter than %d", MIN_EMAIL_LENGTH));
    }
    if (email.length() > MAX_EMAIL_LENGTH) {
      throw new InvalidEmailException(
          String.format("displayId is logger than %d", MAX_EMAIL_LENGTH));
    }
    if (accountRepository.existsByEmail(email)) {
      throw new EmailAlreadyExistException();
    }
  }

  public void addNewToken(Token newToken) {
    List<Token> tokens = filterOldTokens(this.tokens);
    tokens.add(newToken);

    this.tokens = trimTokens(tokens);

    Events.register(new AccountTokenUpdatedEvent(this.id, this.tokens)
        .needAck());
  }

  public void changeToken(@NonNull Token oldToken, @NonNull Token newToken) {
    List<Token> tokens = filterOldTokens(this.tokens);
    tokens = reserveDeletionAndSetChild(tokens, oldToken.getTokenId(), newToken.getTokenId());
    tokens.add(newToken);

    this.tokens = tokens; // 위에서 삭제 예약한 상태이기 때문에 굳이 한 번 더 trim 하지 않는다.

    Events.register(new AccountTokenUpdatedEvent(this.id, this.tokens)
        .needAck());
  }

  public void removeToken(String tokenId) {
    this.tokens = removeTokenById(filterOldTokens(this.tokens), tokenId);
    Events.register(new AccountTokenUpdatedEvent(this.id, this.tokens));
  }

  public void clearTokens() {
    this.tokens = Collections.emptyList();
    Events.register(new AccountTokensClearedEvent(this.id));
  }

  public void delete() {
    Events.register(new AccountDeletedEvent(this.id));
  }

  /**
   * tokens 에서 삭제할 토큰(oldToken)의 exp 를 몇 초 이내로 줄이고, 변경할 토큰(newToken)을 예전 토큰의 child 로 넣는다.
   * 이후 child 가 있는 토큰에 대한 reissue 요청이 왔을 때, 별도 처리 없이 바로 child 를 전달하게 된다.
   */
  private static List<Token> reserveDeletionAndSetChild(List<Token> tokens,
      String oldTokenId, String newTokenId) {

    ZonedDateTime fewSecondsLater = ZonedDateTime.now().plusSeconds(TOKEN_EXP_BUFFER_SECONDS);
    return tokens.stream()
        .map(token -> StringUtils.equals(oldTokenId, token.getTokenId())
            ? new Token(oldTokenId, fewSecondsLater.toEpochSecond(), newTokenId)
            : token)
        .collect(Collectors.toList());
  }

  private static List<Token> removeTokenById(List<Token> tokens, String tokenId) {
    return tokens.stream()
        .filter(token -> !StringUtils.equals(tokenId, token.getTokenId()))
        .collect(Collectors.toList());
  }

  private static List<Token> filterOldTokens(List<Token> tokens) {
    long nowEpochSecond = ZonedDateTime.now().toEpochSecond();
    return tokens.stream()
        .filter(token -> nowEpochSecond < token.getExp())
        .collect(Collectors.toList());
  }

  /**
   * token 개수가 NUMBER_OF_TOKEN_LIMIT 을 넘으면 만료시간이 먼저인 순으로 제거한 후 반환한다.
   */
  private static List<Token> trimTokens(List<Token> tokens) {
    if (tokens.size() > NUMBER_OF_TOKEN_LIMIT) {
      return tokens.stream()
          .sorted(Comparator.comparing(Token::getExp))
          .skip(tokens.size() - NUMBER_OF_TOKEN_LIMIT)
          .collect(Collectors.toList());
    }
    return tokens;
  }

  public void updateAccountOption(AccountOption.DocumentOrderType documentOrderType,
      Integer documentCount) {

    AccountOption.AccountOptionBuilder builder = this.option.toBuilder();
    if (documentOrderType != null) {
      builder.documentOrderType(documentOrderType);
    }
    if (documentCount != null) {
      builder.documentCount(documentCount);
    }

    AccountOption option = builder.build();
    if (this.option.equals(option)) {
      return;
    }
    this.option = option;
    Events.register(new AccountOptionUpdatedEvent(this.id, this.option));
  }

  public void updateAccountTrack(CategoryId recentlyViewedCategoryId) {
    AccountTrack track = new AccountTrack(recentlyViewedCategoryId.getId());
    if (this.track != null && this.track.equals(track)) {
      return;
    }
    this.track = track;
    Events.register(new AccountTrackUpdatedEvent(this.id, this.track));
  }
}
