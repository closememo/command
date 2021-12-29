package com.closememo.command.infra.batch;

import com.closememo.command.application.SystemCommandRequester;
import com.closememo.command.application.category.BatchCategorySetCountCommand;
import com.closememo.command.domain.category.CategoryId;
import com.closememo.command.infra.messageing.publisher.MessagePublisher;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class BatchService {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final MessagePublisher messagePublisher;

  public BatchService(
      NamedParameterJdbcTemplate jdbcTemplate,
      MessagePublisher messagePublisher) {
    this.jdbcTemplate = jdbcTemplate;
    this.messagePublisher = messagePublisher;
  }

  @Transactional
  public void step1() {
    List<Account> accounts = jdbcTemplate.query(
        "SELECT a.id FROM accounts a",
        Map.of(),
        Account.rowMapper());

    accounts.stream()
        .map(Account::getId)
        .forEach(this::step1_1);
  }

  private void step1_1(String accountId) {
    List<Category> categories = jdbcTemplate.query(
        "SELECT c.id FROM categories c WHERE c.owner_id = :ownerId",
        Map.of("ownerId", accountId),
        Category.rowMapper());

    categories.stream()
        .map(Category::getId)
        .forEach(this::step1_2);
  }

  private void step1_2(String categoryId) {
    Integer count = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM documents d WHERE d.category_id = :categoryId",
        Map.of("categoryId", categoryId),
        Integer.class);

    BatchCategorySetCountCommand command = new BatchCategorySetCountCommand(
        SystemCommandRequester.getInstance(), new CategoryId(categoryId),
        count != null ? count : 0);

    messagePublisher.publish(command);
  }

  @Getter
  private static class Account {

    private final String id;

    public Account(String id) {
      this.id = id;
    }

    private static RowMapper<Account> rowMapper() {
      return (rs, rowNum) -> new Account(rs.getString("id"));
    }
  }

  @Getter
  private static class Category {

    private final String id;

    public Category(String id) {
      this.id = id;
    }

    private static RowMapper<Category> rowMapper() {
      return (rs, rowNum) -> new Category(rs.getString("id"));
    }
  }
}
