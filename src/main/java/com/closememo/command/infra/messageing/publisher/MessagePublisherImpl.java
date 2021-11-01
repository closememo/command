package com.closememo.command.infra.messageing.publisher;

import com.closememo.command.config.messaging.integration.IntegrationConfig;
import com.closememo.command.domain.DomainEvent;
import com.closememo.command.infra.helper.JsonUtils;
import com.closememo.command.application.Command;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessagePublisherImpl implements MessagePublisher {

  private final ApplicationContext context;

  public MessagePublisherImpl(ApplicationContext context) {
    this.context = context;
  }

  @Override
  public void publish(DomainEvent domainEvent) {
    String channelName = domainEvent.getClass().getSimpleName();
    send(channelName, new GenericMessage<>(domainEvent));
  }

  @Override
  public void publish(Command command) {
    send(IntegrationConfig.COMMAND_CHANNEL_NAME, new GenericMessage<>(command, Map
        .of(MessageHeaders.REPLY_CHANNEL, "nullChannel")));
  }

  private void send(String channelName, Message<?> message) {
    log.debug("message send: {} : {}", channelName, JsonUtils.toJson(message));
    MessageChannel messageChannel = (MessageChannel) context.getBean(channelName);
    messageChannel.send(message);
  }
}
