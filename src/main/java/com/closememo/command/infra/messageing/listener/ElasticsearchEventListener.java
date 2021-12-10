package com.closememo.command.infra.messageing.listener;

import com.closememo.command.domain.document.AutoTagsUpdatedEvent;
import com.closememo.command.domain.document.DocumentCreatedEvent;
import com.closememo.command.domain.document.DocumentDeletedEvent;
import com.closememo.command.domain.document.DocumentUpdatedEvent;
import com.closememo.command.infra.elasticsearch.ElasticsearchClient;
import com.closememo.command.infra.elasticsearch.request.DeleteAutoTagRequest;
import com.closememo.command.infra.elasticsearch.request.DeletePostRequest;
import com.closememo.command.infra.elasticsearch.request.IndexPostRequest;
import com.closememo.command.infra.elasticsearch.request.UpdatePostRequest;
import com.closememo.command.infra.elasticsearch.request.UpsertAutoTagRequest;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchEventListener {

  private final ElasticsearchClient elasticsearchClient;

  public ElasticsearchEventListener(
      ElasticsearchClient elasticsearchClient) {
    this.elasticsearchClient = elasticsearchClient;
  }

  @ServiceActivator(inputChannel = "DocumentCreatedEvent")
  public void handle(DocumentCreatedEvent payload) {
    IndexPostRequest request = new IndexPostRequest(payload);
    elasticsearchClient.index(request);
  }

  @ServiceActivator(inputChannel = "DocumentUpdatedEvent")
  public void handle(DocumentUpdatedEvent payload) {
    UpdatePostRequest request = new UpdatePostRequest(payload);
    elasticsearchClient.update(request);

    if (!payload.getOption().isHasAutoTag()) {
      DeleteAutoTagRequest deleteAutoTagRequest = new DeleteAutoTagRequest(payload);
      elasticsearchClient.delete(deleteAutoTagRequest);
    }
  }

  @ServiceActivator(inputChannel = "DocumentDeletedEvent")
  public void handle(DocumentDeletedEvent payload) {
    DeletePostRequest deletePostRequest = new DeletePostRequest(payload);
    DeleteAutoTagRequest deleteAutoTagRequest = new DeleteAutoTagRequest(payload);

    elasticsearchClient.delete(deletePostRequest);
    elasticsearchClient.delete(deleteAutoTagRequest);
  }

  @ServiceActivator(inputChannel = "AutoTagsUpdatedEvent")
  public void handle(AutoTagsUpdatedEvent payload) {
    UpsertAutoTagRequest request = new UpsertAutoTagRequest(payload);
    elasticsearchClient.update(request);
  }
}
