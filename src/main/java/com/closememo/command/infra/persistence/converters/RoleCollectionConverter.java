package com.closememo.command.infra.persistence.converters;

import com.closememo.command.domain.account.Role;
import jakarta.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class RoleCollectionConverter implements AttributeConverter<Set<Role>, String> {

  public static final String ROLE_DELIMITER = ":";

  @Override
  public String convertToDatabaseColumn(Set<Role> attribute) {
    if (CollectionUtils.isEmpty(attribute)) {
      return StringUtils.EMPTY;
    }
    return attribute.stream().map(Role::name).collect(Collectors.joining(ROLE_DELIMITER));
  }

  @Override
  public Set<Role> convertToEntityAttribute(String dbData) {
    if (StringUtils.isBlank(dbData)) {
      return new HashSet<>();
    }
    return Arrays.stream(StringUtils.split(dbData, ROLE_DELIMITER))
        .map(Role::valueOf)
        .collect(Collectors.toSet());
  }
}
