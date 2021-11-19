package com.closememo.command.infra.elasticsearch.request;

import com.closememo.command.domain.document.DocumentDeletedEvent;
import lombok.Getter;
import org.elasticsearch.action.delete.DeleteRequest;

@Getter
public class DeletePostRequest {

  private static final String INDEX_NAME = "post";

  private final String id;
  private final DeleteRequest request;

  public DeletePostRequest(DocumentDeletedEvent event) {
    this.id = event.getDocumentId().getId();
    this.request = new DeleteRequest(INDEX_NAME, event.getDocumentId().getId());
  }
}
