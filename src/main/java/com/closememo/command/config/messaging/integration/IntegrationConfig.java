package com.closememo.command.config.messaging.integration;

import com.closememo.command.domain.DomainEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.closememo.command.application.Command;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.EventDrivenConsumer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.support.GenericWebApplicationContext;

@Slf4j
@Configuration
public class IntegrationConfig {

  public static final String COMMAND_CHANNEL_NAME = "CommandChannel";

  @Bean
  public IntegrationFlow commandRoute() {
    return IntegrationFlows.from(COMMAND_CHANNEL_NAME)
        .route((Command o) -> o.getClass().getSimpleName())
        .get();
  }

  @Bean
  public MessageHandler broadcastDomainEventHandler(
      @Qualifier("kafkaObjectMapper") ObjectMapper kafkaObjectMapper,
      KafkaTemplate<String, String> kafkaTemplate) {

    return message -> {
      DomainEvent payload = (DomainEvent) message.getPayload();
      try {
        Message<String> kafkaMessage = MessageBuilder
            .withPayload(kafkaObjectMapper.writeValueAsString(payload))
            .setHeader(KafkaHeaders.TOPIC, payload.getClass().getSimpleName())
            .setHeader(KafkaHeaders.MESSAGE_KEY, payload.getAggregateId())
            .build();
        kafkaTemplate.send(kafkaMessage);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        throw new IllegalArgumentException();
      }
    };
  }

  @Bean
  public CommandClassResolver commandClassResolver() {
    Reflections reflections = new Reflections("com.closememo.command.application");
    return new CommandClassResolver(reflections.getSubTypesOf(Command.class));
  }

  @Autowired
  private void registerCommandChannels(GenericWebApplicationContext context,
      @Qualifier("messageTaskExecutor") ThreadPoolTaskExecutor messageTaskExecutor) {
    commandClassResolver().getNames()
        .forEach(name -> context.registerBean(name, ExecutorChannel.class,
            () -> new ExecutorChannel(messageTaskExecutor)));
  }

  @Autowired
  private void registerDomainEventChannels(GenericWebApplicationContext context,
      @Qualifier("messageTaskExecutor") ThreadPoolTaskExecutor messageTaskExecutor,
      @Qualifier("broadcastDomainEventHandler") MessageHandler broadcastDomainEventHandler) {
    Reflections reflections = new Reflections("com.closememo.command.domain");
    reflections.getSubTypesOf(DomainEvent.class).forEach(aClass -> {
      String eventChannelName = aClass.getSimpleName();

      context.registerBean(eventChannelName, PublishSubscribeChannel.class,
          () -> new PublishSubscribeChannel(messageTaskExecutor));
      PublishSubscribeChannel eventChannel = context.getBean(eventChannelName, PublishSubscribeChannel.class);
      context.registerBean("Broadcast-" + eventChannelName, EventDrivenConsumer.class,
          eventChannel, broadcastDomainEventHandler);
    });
  }
}
