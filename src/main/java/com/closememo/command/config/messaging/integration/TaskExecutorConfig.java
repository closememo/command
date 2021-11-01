package com.closememo.command.config.messaging.integration;

import com.zaxxer.hikari.HikariConfig;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TaskExecutorConfig {

  private static final float MESSAGE_DB_CONN_RATIO = 0.5f;

  @Bean
  public ThreadPoolTaskExecutor messageTaskExecutor(HikariConfig hikariConfig) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(calcPoolSize(hikariConfig.getMaximumPoolSize(), MESSAGE_DB_CONN_RATIO));
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.setAllowCoreThreadTimeOut(false);
    executor.setThreadNamePrefix("MSG-EXE-");
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(30);
    executor.initialize();

    return executor;
  }

  private int calcPoolSize(int maximumDbConnection, float ratio) {
    int size = (int) (0.5 + maximumDbConnection * ratio);
    return size == 0 ? 1 : size;
  }
}
