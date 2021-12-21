package com.closememo.command.infra.batch;

import com.closememo.command.application.SystemCommandRequester;
import com.closememo.command.application.category.CreateRootCategoryCommand;
import com.closememo.command.application.document.ChangeDocumentsCategoryCommand;
import com.closememo.command.domain.account.AccountId;
import com.closememo.command.domain.category.CategoryId;
import com.closememo.command.domain.document.Document;
import com.closememo.command.domain.document.DocumentId;
import com.closememo.command.domain.document.DocumentRepository;
import com.closememo.command.infra.messageing.publisher.MessagePublisher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
  private final DocumentRepository documentRepository;

  public BatchService(
      NamedParameterJdbcTemplate jdbcTemplate,
      MessagePublisher messagePublisher,
      DocumentRepository documentRepository) {
    this.jdbcTemplate = jdbcTemplate;
    this.messagePublisher = messagePublisher;
    this.documentRepository = documentRepository;
  }

  public List<String> step1() {
    List<Account> accounts = jdbcTemplate.query(
        "SELECT a.id FROM accounts a"
            + " LEFT JOIN categories c ON a.id = c.owner_id"
            + " WHERE c.owner_id IS NULL",
        Map.of(),
        Account.rowMapper());

    accounts.forEach(account -> {
          AccountId accountId = new AccountId(account.id);
          CreateRootCategoryCommand command =
              new CreateRootCategoryCommand(SystemCommandRequester.getInstance(), accountId);
          messagePublisher.publish(command);
        }
    );

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return accounts.stream()
        .map(Account::getId)
        .collect(Collectors.toList());
  }

  @Transactional
  public void step2(String accountId) {
    Category category = jdbcTemplate.queryForObject(
        "SELECT c.id FROM categories c WHERE c.owner_id = :ownerId AND c.is_root = TRUE",
        Map.of("ownerId", accountId),
        Category.rowMapper());

    if (category == null) {
      return;
    }

    List<DocumentId> documentIds = new ArrayList<>();
    try (Stream<Document> documents = documentRepository.findAllByOwnerId(new AccountId(accountId))) {
      documents.map(Document::getId)
          .forEach(documentIds::add);
    }

    CategoryId categoryId = new CategoryId(category.id);
    ChangeDocumentsCategoryCommand command =
        new ChangeDocumentsCategoryCommand(SystemCommandRequester.getInstance(), documentIds, categoryId);

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
