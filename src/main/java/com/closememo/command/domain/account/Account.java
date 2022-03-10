package com.closememo.command.domain.account;

import com.closememo.command.domain.Events;
import com.closememo.command.domain.category.CategoryId;
import com.closememo.command.infra.persistence.converters.RoleCollectionConverter;
import java.time.ZonedDateTime;
import java.util.Collections;
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
    List<Token> tokens = removeOldTokens(this.tokens);
    tokens.add(newToken);

    this.tokens = tokens;

    Events.register(new AccountTokenUpdatedEvent(this.id, this.tokens)
        .needAck());
  }

  public void changeToken(String oldTokenId, Token newToken) {
    List<Token> tokens = reserveDeletion(removeOldTokens(this.tokens),
        oldTokenId, newToken.getTokenId());
    tokens.add(newToken);

    this.tokens = tokens;

    Events.register(new AccountTokenUpdatedEvent(this.id, this.tokens)
        .needAck());
  }

  public void removeToken(String tokenId) {
    this.tokens = removeTokenById(removeOldTokens(this.tokens), tokenId);

    Events.register(new AccountTokenUpdatedEvent(this.id, this.tokens));
  }

  public void clearTokens() {
    this.tokens = Collections.emptyList();

    Events.register(new AccountTokensClearedEvent(this.id));
  }

  public void delete() {
    Events.register(new AccountDeletedEvent(this.id));
  }

  private static List<Token> reserveDeletion(List<Token> tokens,
      String oldTokenId, String newTokenId) {

    ZonedDateTime tenSecondsLater = ZonedDateTime.now().plusSeconds(10L);

    return tokens.stream()
        .map(token -> StringUtils.equals(oldTokenId, token.getTokenId())
            ? new Token(oldTokenId, tenSecondsLater.toEpochSecond(), newTokenId)
            : token)
        .collect(Collectors.toList());
  }

  private static List<Token> removeTokenById(List<Token> tokens, String tokenId) {
    return tokens.stream()
        .filter(token -> !StringUtils.equals(tokenId, token.getTokenId()))
        .collect(Collectors.toList());
  }

  private static List<Token> removeOldTokens(List<Token> tokens) {
    long nowEpochSecond = ZonedDateTime.now().toEpochSecond();
    long buffer = 10L;

    return tokens.stream()
        .filter(token ->
            nowEpochSecond - buffer < token.getExp())
        .collect(Collectors.toList());
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
