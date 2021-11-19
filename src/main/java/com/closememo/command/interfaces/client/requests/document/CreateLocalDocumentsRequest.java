package com.closememo.command.interfaces.client.requests.document;

import java.util.List;
import lombok.Getter;

@Getter
public class CreateLocalDocumentsRequest {

  private List<LocalDocument> localDocuments;

  @Getter
  public static class LocalDocument {

    private String title;
    private String content;
    private String localFormedDateString;
  }
}
