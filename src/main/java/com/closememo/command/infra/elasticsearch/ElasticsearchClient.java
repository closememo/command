package com.closememo.command.infra.elasticsearch;

import com.closememo.command.infra.elasticsearch.request.interfaces.DeleteESRequest;
import com.closememo.command.infra.elasticsearch.request.interfaces.IndexESRequest;
import com.closememo.command.infra.elasticsearch.request.interfaces.UpdateESRequest;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ElasticsearchClient {

  private final RestHighLevelClient client;

  public ElasticsearchClient(RestHighLevelClient client) {
    this.client = client;
  }

  public void index(IndexESRequest request) {
    log.debug("[ELASTICSEARCH] request: " + request.getRequest().toString());
    IndexResponse response;
    try {
      response = client.index(request.getRequest(), RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new ElasticsearchClientException();
    }
    log.debug("[ELASTICSEARCH] response: " + response.toString());
    if (!RestStatus.CREATED.equals(response.status())) {
      throw new ElasticsearchClientException();
    }
  }

  public void update(UpdateESRequest request) {
    log.debug("[ELASTICSEARCH] request: " + request.getRequest().toString());
    UpdateResponse response;
    try {
      response = client.update(request.getRequest(), RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new ElasticsearchClientException();
    }
    log.debug("[ELASTICSEARCH] response: " + response.toString());
    if (RestStatus.NOT_FOUND.equals(response.status())) {
      log.warn("Document does not exist. id=" + request.getId());
    }
  }

  public void delete(DeleteESRequest request) {
    log.debug("[ELASTICSEARCH] request: " + request.getRequest().toString());
    DeleteResponse response;
    try {
      response = client.delete(request.getRequest(), RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new ElasticsearchClientException();
    }
    log.debug("[ELASTICSEARCH] response: " + response.toString());
    if (RestStatus.NOT_FOUND.equals(response.status())) {
      log.warn("Document does not exist. id=" + request.getId());
    }
  }
}
