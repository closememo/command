package com.closememo.command.config.elasticsearch;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClient.FailureListener;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ElasticsearchConfig {

  @Bean
  public RestHighLevelClient restHighLevelClient(ElasticsearchProperty property) {

    HttpHost httpHost = new HttpHost(property.getHostName(), property.getPort());

    RestClientBuilder builder = RestClient.builder(httpHost)
        .setRequestConfigCallback(requestConfigBuilder ->
            requestConfigBuilder
                .setConnectTimeout(property.getConnectionTimeout())
                .setSocketTimeout(property.getSocketTimeout()))
        .setFailureListener(new FailureListener() {
          @Override
          public void onFailure(Node node) {
            log.error("Failure occurred. node=" + node.toString());
          }
        });

    return new RestHighLevelClient(builder);
  }
}
