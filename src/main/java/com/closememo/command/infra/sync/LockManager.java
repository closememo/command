package com.closememo.command.infra.sync;

import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * key 로 구별 할 수 있는, lock 을 관리한다.
 */
@Slf4j
@Component
public class LockManager {

  private final ConcurrentHashMap<Integer, CountableLock> lockHolderMap;

  public LockManager() {
    this.lockHolderMap = new ConcurrentHashMap<>();
  }

  public void lock(Integer key) {
    CountableLock lock = lockHolderMap.get(key);
    if (lock == null) {
      lock = new CountableLock();
      lockHolderMap.put(key, lock);
    }
    lock.increaseCount();
    lock.lock();
  }

  public void unlock(Integer key) {
    CountableLock lock = lockHolderMap.get(key);
    if (lock == null) {
      log.error("[LOG] lock doesn't exist. key=" + key);
      throw new RuntimeException();
    }
    if (lock.getCount() == 1) {
      lockHolderMap.remove(key);
    } else {
      lock.decreaseCount();
    }
    lock.unlock();
  }
}
