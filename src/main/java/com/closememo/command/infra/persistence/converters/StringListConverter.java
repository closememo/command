package com.closememo.command.infra.persistence.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.closememo.command.infra.helper.JsonUtils;
import java.util.List;
import javax.persistence.AttributeConverter;

public class StringListConverter implements AttributeConverter<List<String>, String> {

  @Override
  public String convertToDatabaseColumn(List<String> attribute) {
    return JsonUtils.toJson(attribute);
  }

  @Override
  public List<String> convertToEntityAttribute(String dbData) {
    return JsonUtils.fromJson(dbData, new TypeReference<>() {
    });
  }
}
