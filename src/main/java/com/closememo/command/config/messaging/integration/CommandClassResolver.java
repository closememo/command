package com.closememo.command.config.messaging.integration;

import com.closememo.command.application.Command;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandClassResolver {

  private final Map<String, Class<? extends Command>> classMap;

  public CommandClassResolver(Set<Class<? extends Command>> commandClassSet) {
    this.classMap = commandClassSet.stream()
        .collect(Collectors.toMap(Class::getSimpleName, Function.identity()));
  }

  public Set<String> getNames() {
    return this.classMap.keySet();
  }

  public Class<? extends Command> resolve(String commandName) {
    return this.classMap.get(commandName);
  }
}
