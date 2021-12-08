package com.closememo.command.infra.elasticsearch.request.interfaces;

import org.elasticsearch.action.update.UpdateRequest;

public interface UpdateESRequest {

  UpdateRequest getRequest();

  String getId();
}
