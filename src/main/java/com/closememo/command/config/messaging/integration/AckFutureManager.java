package com.closememo.command.config.messaging.integration;

import java.util.Map.Entry;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class AckFutureManager {

  private static final long SLEEP_TIMEOUT = 7000;
  private static final long WAIT_TIMEOUT = 5000;

  private final ConcurrentMap<String, Future<?>> ackWaitMap;
  private final ThreadPoolTaskExecutor ackEventTaskExecutor;

  public AckFutureManager(
      @Qualifier("ackEventTaskExecutor") ThreadPoolTaskExecutor ackEventTaskExecutor) {
    this.ackWaitMap = new ConcurrentHashMap<>();
    this.ackEventTaskExecutor = ackEventTaskExecutor;
  }

  public void submit(String aggregateId) {
    Future<?> future = ackEventTaskExecutor.submit(() -> {
      try {
        Thread.sleep(SLEEP_TIMEOUT);
      } catch (InterruptedException e) {
        // wake 시 InterruptedException 발생
        return;
      }
      throw new AckEventException("inner sleep timeout");
    });
    ackWaitMap.put(aggregateId, future);
  }

  public void wait(String aggregateId) {
    Future<?> future = ackWaitMap.get(aggregateId);
    if (future == null) {
      return;
    }
    try {
      future.get(WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (InterruptedException | CancellationException e) {
      // wake 시 CancellationException 발생.
      // do nothing
    } catch (ExecutionException e) {
      // Runnable.run() 에서 발생한 AckEventException 을 확인. 처리를 위해 다시 throw.
      if (e.getCause() instanceof AckEventException) {
        throw new AckEventException("sleep timeout");
      }
    } catch (TimeoutException e) {
      // future.get 에서 발생한 timeout.
      throw new AckEventException("wait timeout");
    }
    removeFinished();
  }

  public void wake(String aggregateId) {
    Future<?> future = ackWaitMap.get(aggregateId);
    if (future != null) {
      future.cancel(true);
    }
    removeFinished();
  }

  private void removeFinished() {
    for (Entry<String, Future<?>> entry : ackWaitMap.entrySet()) {
      Future<?> future = entry.getValue();
      if (future.isCancelled() || future.isDone()) {
        ackWaitMap.remove(entry.getKey());
      }
    }
  }
}
