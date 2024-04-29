package com.closememo.command.config.http;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackages = "com.closememo.command.infra.http")
@Configuration
public class FeignConfig {

}
