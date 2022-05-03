package com.closememo.command.config.mdc;

import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

/**
 * ThreadPoolTaskExecutor 를 통해 만들어지는 Thread 에도 MDC 정보를 전달한다.
 */
public class MdcTaskDecorator implements TaskDecorator {

  @Override
  public Runnable decorate(Runnable runnable) {
    Map<String, String> contextMap = MDC.getCopyOfContextMap();
    return () -> {
      if (MapUtils.isNotEmpty(contextMap)) {
        MDC.setContextMap(contextMap);
      }
      runnable.run();
    };
  }
}
