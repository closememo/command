package com.closememo.command.infra.sync;

import java.util.concurrent.locks.ReentrantLock;

public class CountableLock {

  private final ReentrantLock lock;
  private int count;

  public CountableLock() {
    this.lock = new ReentrantLock();
    this.count = 0;
  }

  public void lock() {
    lock.lock();
  }

  public void unlock() {
    lock.unlock();
  }

  public int getCount() {
    return count;
  }

  public void increaseCount() {
    count += 1;
  }

  public void decreaseCount() {
    count -= 1;
  }
}
