package com.closememo.command.infra.elasticsearch.request;

import com.closememo.command.domain.document.DocumentUpdatedEvent;
import com.closememo.command.infra.elasticsearch.request.interfaces.PostESRequest;
import com.closememo.command.infra.elasticsearch.request.interfaces.UpdateESRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.elasticsearch.action.update.UpdateRequest;

@Getter
public class UpdatePostRequest implements PostESRequest, UpdateESRequest {

  private final String id;
  private final UpdateRequest request;

  public UpdatePostRequest(DocumentUpdatedEvent event) {
    this.id = event.getDocumentId().getId();

    Map<String, Object> jsonMap = new HashMap<>();
    jsonMap.put("title", event.getTitle());
    jsonMap.put("content", event.getContent());
    jsonMap.put("tags", event.getTags());

    this.request = new UpdateRequest(INDEX_NAME, event.getDocumentId().getId())
        .doc(jsonMap);
  }
}
