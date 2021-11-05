package com.closememo.command.infra.elasticsearch;

import com.closememo.command.infra.elasticsearch.request.DeletePostRequest;
import com.closememo.command.infra.elasticsearch.request.IndexPostRequest;
import com.closememo.command.infra.elasticsearch.request.UpdatePostRequest;
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

  public void indexPost(IndexPostRequest indexPostRequest) {
    log.debug("[ELASTICSEARCH] request: " + indexPostRequest.getRequest().toString());
    IndexResponse response;
    try {
      response = client.index(indexPostRequest.getRequest(), RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new ElasticsearchClientException();
    }
    log.debug("[ELASTICSEARCH] response: " + response.toString());
    if (!RestStatus.CREATED.equals(response.status())) {
      throw new ElasticsearchClientException();
    }
  }

  public void updatePost(UpdatePostRequest updatePostRequest) {
    log.debug("[ELASTICSEARCH] request: " + updatePostRequest.getRequest().toString());
    UpdateResponse response;
    try {
      response = client.update(updatePostRequest.getRequest(), RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new ElasticsearchClientException();
    }
    log.debug("[ELASTICSEARCH] response: " + response.toString());
    if (RestStatus.NOT_FOUND.equals(response.status())) {
      log.warn("Document does not exist. id=" + updatePostRequest.getId());
    }
  }

  public void deletePost(DeletePostRequest deletePostRequest) {
    log.debug("[ELASTICSEARCH] request: " + deletePostRequest.getRequest().toString());
    DeleteResponse response;
    try {
      response = client.delete(deletePostRequest.getRequest(), RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new ElasticsearchClientException();
    }
    log.debug("[ELASTICSEARCH] response: " + response.toString());
    if (RestStatus.NOT_FOUND.equals(response.status())) {
      log.warn("Document does not exist. id=" + deletePostRequest.getId());
    }
  }
}
