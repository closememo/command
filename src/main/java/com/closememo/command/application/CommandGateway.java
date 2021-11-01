package com.closememo.command.application;

import com.closememo.command.config.messaging.integration.IntegrationConfig;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.stereotype.Component;

@Component
@MessagingGateway(
    defaultRequestChannel = IntegrationConfig.COMMAND_CHANNEL_NAME,
    defaultReplyTimeout = "5000"
)
public interface CommandGateway {

  <T> T request(Command command);

  @Gateway(payloadExpression = "#args[0]", replyTimeoutExpression = "#args[1]")
  <T> T request(Command command, long replyTimeout);
}
