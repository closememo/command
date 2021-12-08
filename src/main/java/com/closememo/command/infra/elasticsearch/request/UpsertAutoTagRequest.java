package com.closememo.command.infra.elasticsearch.request;

import com.closememo.command.domain.document.AutoTagsUpdatedEvent;
import com.closememo.command.infra.elasticsearch.request.interfaces.AutoTagESRequest;
import com.closememo.command.infra.elasticsearch.request.interfaces.UpdateESRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.elasticsearch.action.update.UpdateRequest;

@Getter
public class UpsertAutoTagRequest implements AutoTagESRequest, UpdateESRequest {

  private final String id;
  private final UpdateRequest request;

  public UpsertAutoTagRequest(AutoTagsUpdatedEvent event) {
    this.id = event.getDocumentId().getId();

    Map<String, Object> newAutoTags = new HashMap<>();
    newAutoTags.put("autoTags", event.getAutoTags());

    Map<String, Object> newDocument = new HashMap<>();
    newDocument.put("id", event.getDocumentId().getId());
    newDocument.put("ownerId", event.getOwnerId().getId());
    newDocument.put("autoTags", event.getAutoTags());

    this.request = new UpdateRequest(INDEX_NAME, event.getDocumentId().getId())
        .doc(newAutoTags)
        .upsert(newDocument);
  }
}
