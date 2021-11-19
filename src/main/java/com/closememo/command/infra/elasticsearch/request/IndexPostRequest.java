package com.closememo.command.infra.elasticsearch.request;

import com.closememo.command.domain.document.DocumentCreatedEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.elasticsearch.action.index.IndexRequest;

@Getter
public class IndexPostRequest {

  private static final String INDEX_NAME = "post";

  private final IndexRequest request;

  public IndexPostRequest(DocumentCreatedEvent event) {
    Map<String, Object> jsonMap = new HashMap<>();
    jsonMap.put("id", event.getDocumentId().getId());
    jsonMap.put("ownerId", event.getOwnerId().getId());
    jsonMap.put("title", event.getTitle());
    jsonMap.put("content", event.getContent());
    jsonMap.put("tags", event.getTags());
    jsonMap.put("createdAt", event.getCreatedAt());

    this.request = new IndexRequest(INDEX_NAME)
        .id(event.getDocumentId().getId())
        .source(jsonMap);
  }
}
