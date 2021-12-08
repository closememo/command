package com.closememo.command.infra.elasticsearch.request.interfaces;

import org.elasticsearch.action.delete.DeleteRequest;

public interface DeleteESRequest {

  DeleteRequest getRequest();

  String getId();
}
