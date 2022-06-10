package com.closememo.command.infra.sync;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class CountableLock {

  private final ReentrantLock lock;
  private final AtomicInteger count;

  public CountableLock() {
    this.lock = new ReentrantLock();
    this.count = new AtomicInteger();
  }

  public void lock() {
    lock.lock();
  }

  public void unlock() {
    lock.unlock();
  }

  public int getCount() {
    return count.get();
  }

  public void increaseCount() {
    count.incrementAndGet();
  }

  public void decreaseCount() {
    count.decrementAndGet();
  }
}
