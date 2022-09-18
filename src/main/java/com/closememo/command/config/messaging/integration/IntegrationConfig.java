package com.closememo.command.config.messaging.integration;

import static org.reflections.scanners.Scanners.SubTypes;

import com.closememo.command.application.Command;
import com.closememo.command.config.messaging.kafka.KafkaMessageConverter;
import com.closememo.command.domain.DomainEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.EventDrivenConsumer;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.integration.router.HeaderValueRouter;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
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
  public KafkaMessageDrivenChannelAdapter<?, ?> kafkaMessageDrivenAdapter(
      @Qualifier("kafkaListenerContainerFactory") KafkaListenerContainerFactory<?> kafkaListenerContainerFactory,
      @Qualifier("kafkaMessageConverter") KafkaMessageConverter kafkaMessageConverter) {

    String[] topics = new String[] {"AckEvent"};
    ConcurrentMessageListenerContainer<?, ?> container
        = (ConcurrentMessageListenerContainer<?, ?>) kafkaListenerContainerFactory
        .createContainer(topics);
    KafkaMessageDrivenChannelAdapter<?, ?> kafkaMessageDrivenChannelAdapter
        = new KafkaMessageDrivenChannelAdapter<>(container);
    kafkaMessageDrivenChannelAdapter.setMessageConverter(kafkaMessageConverter);
    kafkaMessageDrivenChannelAdapter.setOutputChannel(inboundKafkaMessageChannel());

    return kafkaMessageDrivenChannelAdapter;
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
  public MessageChannel inboundKafkaMessageChannel() {
    return new DirectChannel();
  }

  @Bean
  public IntegrationFlow routeKafkaInboundMessage() {
    return IntegrationFlows.from(inboundKafkaMessageChannel())
        .route(router())
        .get();
  }

  private HeaderValueRouter router() {
    return new HeaderValueRouter(KafkaHeaders.RECEIVED_TOPIC);
  }

  @Component
  public static class Initializer {

    private final GenericWebApplicationContext context;
    private final ThreadPoolTaskExecutor messageTaskExecutor;
    private final MessageHandler broadcastDomainEventHandler;

    public Initializer(
        GenericWebApplicationContext context,
        @Qualifier("messageTaskExecutor") ThreadPoolTaskExecutor messageTaskExecutor,
        @Qualifier("broadcastDomainEventHandler") MessageHandler broadcastDomainEventHandler) {
      this.context = context;
      this.messageTaskExecutor = messageTaskExecutor;
      this.broadcastDomainEventHandler = broadcastDomainEventHandler;
    }

    @PostConstruct
    public void init() {
      registerCommandChannels(context, messageTaskExecutor);
      registerDomainEventChannels(context, messageTaskExecutor, broadcastDomainEventHandler);
    }

    private void registerCommandChannels(GenericWebApplicationContext context,
        ThreadPoolTaskExecutor messageTaskExecutor) {

      Reflections reflections = new Reflections(new ConfigurationBuilder()
          .setUrls(ClasspathHelper.forClass(Command.class))
          .setScanners(SubTypes));

      reflections.getSubTypesOf(Command.class)
          .forEach(aClass -> context.registerBean(aClass.getSimpleName(), ExecutorChannel.class,
              () -> new ExecutorChannel(messageTaskExecutor)));
    }

    private void registerDomainEventChannels(GenericWebApplicationContext context,
        ThreadPoolTaskExecutor messageTaskExecutor, MessageHandler broadcastDomainEventHandler) {

      Reflections reflections = new Reflections(new ConfigurationBuilder()
          .setUrls(ClasspathHelper.forClass(DomainEvent.class))
          .setScanners(SubTypes));

      reflections.getSubTypesOf(DomainEvent.class)
          .forEach(aClass -> {
            String eventChannelName = aClass.getSimpleName();

            context.registerBean(eventChannelName, PublishSubscribeChannel.class,
                () -> new PublishSubscribeChannel(messageTaskExecutor));
            PublishSubscribeChannel eventChannel = context
                .getBean(eventChannelName, PublishSubscribeChannel.class);
            context.registerBean("Broadcast-" + eventChannelName, EventDrivenConsumer.class,
                eventChannel, broadcastDomainEventHandler);
          });
    }
  }
}
