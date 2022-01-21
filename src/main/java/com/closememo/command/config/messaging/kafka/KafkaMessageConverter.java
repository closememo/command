package com.closememo.command.config.messaging.kafka;

import com.closememo.command.infra.helper.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.kafka.support.converter.ConversionException;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.lang.NonNull;

@Slf4j
public class KafkaMessageConverter extends StringJsonMessageConverter {

  private final Map<String, Class<?>> topicClassMap;
  private final Class<?> defaultType;

  public KafkaMessageConverter(ObjectMapper objectMapper, Map<String, Class<?>> topicClassMap) {
    this(objectMapper, topicClassMap, null);
  }

  public KafkaMessageConverter(ObjectMapper objectMapper,
      Map<String, Class<?>> topicClassMap, Class<?> defaultType) {
    super(objectMapper);
    this.topicClassMap = topicClassMap;
    this.defaultType = defaultType;
  }

  @Override
  protected Object extractAndConvertValue(ConsumerRecord<?, ?> record, Type type) {
    Object value = record.value();

    if (record.value() == null) {
      return KafkaNull.INSTANCE;
    }

    String topic = record.topic();

    Class<?> clazz = Optional.ofNullable(topicClassMap)
        .<Class<?>>map(topicCache -> topicCache.get(topic))
        .orElseGet(() -> this.getClassFromType(type));

    if (clazz == null) {
      throw new KafkaMessageConverterException("Failed to convert message. TOPIC : " + record.topic());
    }

    Object event = convertValue(value, clazz);

    if (log.isDebugEnabled()) {
      log.debug("message received: {} / {}", topic, JsonUtils.toJson(event));
    }

    return event;
  }

  private Class<?> getClassFromType(@NonNull Type type) {
    try {
      return Class.forName(type.getTypeName());
    } catch (ClassNotFoundException e) {
      return Optional.ofNullable(this.defaultType)
          .orElseThrow(() -> new KafkaMessageConverterException(String.format("Cannot find class: %s", type.getTypeName()), e));
    }
  }

  private Object convertValue(Object value, Class<?> clazz) {
    if (clazz.equals(String.class)) {
      return value;
    }

    try {
      return super.getObjectMapper().readValue((String) value, clazz);
    } catch (IOException e) {
      throw new ConversionException("Failed to convert from JSON", e);
    }
  }
}
