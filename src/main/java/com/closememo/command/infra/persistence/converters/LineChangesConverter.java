package com.closememo.command.infra.persistence.converters;

import com.closememo.command.domain.difference.LineDelta;
import com.fasterxml.jackson.core.type.TypeReference;
import com.closememo.command.infra.helper.JsonUtils;
import jakarta.persistence.AttributeConverter;
import java.util.List;

public class LineChangesConverter implements AttributeConverter<List<LineDelta>, String> {

  @Override
  public String convertToDatabaseColumn(List<LineDelta> attribute) {
    return JsonUtils.toJson(attribute);
  }

  @Override
  public List<LineDelta> convertToEntityAttribute(String dbData) {
    return JsonUtils.fromJson(dbData, new TypeReference<>() {
    });
  }
}
