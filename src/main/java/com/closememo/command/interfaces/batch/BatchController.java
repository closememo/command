package com.closememo.command.interfaces.batch;

import com.closememo.command.infra.batch.BatchService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;

@BatchCommandInterface
public class BatchController {

  private final BatchService batchService;

  public BatchController(BatchService batchService) {
    this.batchService = batchService;
  }

  @GetMapping("/run")
  public void run() {
    List<String> accountIds = batchService.step1();
    accountIds.forEach(batchService::step2);
  }
}
