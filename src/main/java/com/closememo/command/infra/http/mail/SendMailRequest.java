package com.closememo.command.infra.http.mail;

import com.closememo.command.domain.document.Document;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class SendMailRequest {

  private final String email;
  private final List<Post> posts;

  public SendMailRequest(String email,
      List<Document> documents) {
    this.email = email;
    this.posts = documents.stream()
        .map(Post::new)
        .collect(Collectors.toList());
  }

  @Getter
  public static class Post {

    private final String title;
    private final String content;
    private final List<String> tags;
    private final ZonedDateTime createdAt;

    public Post(Document document) {
      this.title = document.getTitle();
      this.content = document.getContent();
      this.tags = document.getTags();
      this.createdAt = document.getCreatedAt();
    }
  }
}
