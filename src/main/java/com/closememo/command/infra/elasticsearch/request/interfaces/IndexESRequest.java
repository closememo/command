package com.closememo.command.infra.elasticsearch.request.interfaces;

import org.elasticsearch.action.index.IndexRequest;

public interface IndexESRequest {

  IndexRequest getRequest();
}
