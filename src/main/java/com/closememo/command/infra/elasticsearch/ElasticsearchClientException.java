package com.closememo.command.infra.elasticsearch;

import com.closememo.command.domain.InternalServerException;

public class ElasticsearchClientException extends InternalServerException {

  private static final long serialVersionUID = 2518635422787326580L;

  public ElasticsearchClientException() {
  }

  public ElasticsearchClientException(String message) {
    super(message);
  }
}
