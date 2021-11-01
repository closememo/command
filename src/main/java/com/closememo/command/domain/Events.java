package com.closememo.command.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Events {

  private static final ThreadLocal<List<DomainEvent>> eventsThreadLocal
      = ThreadLocal.withInitial(ArrayList::new);

  public static void register(DomainEvent event) {
    if (event == null) {
      return;
    }
    eventsThreadLocal.get().add(event);
  }

  public static List<DomainEvent> getDomainEvents() {
    return eventsThreadLocal.get();
  }

  public static void clear() {
    eventsThreadLocal.get().clear();
  }
}
