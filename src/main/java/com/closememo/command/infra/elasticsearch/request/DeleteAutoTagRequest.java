package com.closememo.command.infra.elasticsearch.request;

import com.closememo.command.domain.document.DocumentDeletedEvent;
import com.closememo.command.infra.elasticsearch.request.interfaces.AutoTagESRequest;
import com.closememo.command.infra.elasticsearch.request.interfaces.DeleteESRequest;
import lombok.Getter;
import org.elasticsearch.action.delete.DeleteRequest;

@Getter
public class DeleteAutoTagRequest implements DeleteESRequest, AutoTagESRequest {

  private final String id;
  private final DeleteRequest request;

  public DeleteAutoTagRequest(DocumentDeletedEvent event) {
    this.id = event.getDocumentId().getId();
    this.request = new DeleteRequest(INDEX_NAME, event.getDocumentId().getId());
  }
}
