package com.closememo.command.interfaces.batch;

import com.closememo.command.infra.batch.BatchService;
import org.springframework.web.bind.annotation.GetMapping;

@BatchCommandInterface
public class BatchController {

  private final BatchService batchService;

  public BatchController(BatchService batchService) {
    this.batchService = batchService;
  }

  @GetMapping("/run")
  public void run() {
    batchService.step1();
  }
}
