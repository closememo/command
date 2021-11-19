package com.closememo.command.infra.messageing.listener;

import com.closememo.command.domain.document.DocumentCreatedEvent;
import com.closememo.command.domain.document.DocumentDeletedEvent;
import com.closememo.command.domain.document.DocumentUpdatedEvent;
import com.closememo.command.infra.elasticsearch.ElasticsearchClient;
import com.closememo.command.infra.elasticsearch.request.DeletePostRequest;
import com.closememo.command.infra.elasticsearch.request.IndexPostRequest;
import com.closememo.command.infra.elasticsearch.request.UpdatePostRequest;
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
    elasticsearchClient.indexPost(request);
  }

  @ServiceActivator(inputChannel = "DocumentUpdatedEvent")
  public void handle(DocumentUpdatedEvent payload) {

    UpdatePostRequest request = new UpdatePostRequest(payload);
    elasticsearchClient.updatePost(request);
  }

  @ServiceActivator(inputChannel = "DocumentDeletedEvent")
  public void handle(DocumentDeletedEvent payload) {

    DeletePostRequest request = new DeletePostRequest(payload);
    elasticsearchClient.deletePost(request);
  }
}
