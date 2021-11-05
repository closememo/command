package com.closememo.command.infra.elasticsearch.request;

import com.closememo.command.domain.document.Document;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.elasticsearch.action.index.IndexRequest;

@Getter
public class IndexPostRequest {

  private static final String INDEX_NAME = "post";

  private final IndexRequest request;

  public IndexPostRequest(Document document) {

    Map<String, Object> jsonMap = new HashMap<>();
    jsonMap.put("id", document.getId().getId());
    jsonMap.put("ownerId", document.getOwnerId().getId());
    jsonMap.put("title", document.getTitle());
    jsonMap.put("content", document.getContent());
    jsonMap.put("tags", document.getTags());
    jsonMap.put("createdAt", document.getCreatedAt());

    this.request = new IndexRequest(INDEX_NAME)
        .id(document.getId().getId())
        .source(jsonMap);
  }
}
