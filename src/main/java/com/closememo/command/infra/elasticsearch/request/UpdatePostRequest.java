package com.closememo.command.infra.elasticsearch.request;

import com.closememo.command.domain.document.Document;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.elasticsearch.action.update.UpdateRequest;

@Getter
public class UpdatePostRequest {

  private static final String INDEX_NAME = "post";

  private final String id;
  private final UpdateRequest request;

  public UpdatePostRequest(Document document) {

    this.id = document.getId().getId();

    Map<String, Object> jsonMap = new HashMap<>();
    jsonMap.put("title", document.getTitle());
    jsonMap.put("content", document.getContent());
    jsonMap.put("tags", document.getTags());

    this.request = new UpdateRequest(INDEX_NAME, document.getId().getId())
        .doc(jsonMap);
  }
}
