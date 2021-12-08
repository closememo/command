package com.closememo.command.infra.elasticsearch.request;

import com.closememo.command.domain.document.AutoTagsUpdatedEvent;
import com.closememo.command.infra.elasticsearch.request.interfaces.AutoTagESRequest;
import com.closememo.command.infra.elasticsearch.request.interfaces.IndexESRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.elasticsearch.action.index.IndexRequest;

@Getter
public class IndexAutoTagRequest implements AutoTagESRequest, IndexESRequest {

  private final IndexRequest request;

  public IndexAutoTagRequest(AutoTagsUpdatedEvent event) {
    Map<String, Object> jsonMap = new HashMap<>();
    jsonMap.put("id", event.getDocumentId().getId());
    jsonMap.put("ownerId", event.getOwnerId().getId());
    jsonMap.put("autoTags", event.getAutoTags());

    this.request = new IndexRequest(INDEX_NAME)
        .id(event.getDocumentId().getId())
        .source(jsonMap);
  }
}
