package com.closememo.command.config.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI apiOpenAPI() {
    return new OpenAPI()
        .servers(List.of(new Server().url("/")))
        .components(new Components()
            .addSecuritySchemes("Account", new SecurityScheme()
                .name("X-Bypass-Account-Id")
                .type(Type.APIKEY)
                .in(In.HEADER))
            .addSecuritySchemes("System", new SecurityScheme()
                .name("X-SYSTEM-KEY")
                .type(Type.APIKEY)
                .in(In.HEADER)))
        .security(List.of(new SecurityRequirement().addList("Account"),
            new SecurityRequirement().addList("System")))
        .info(new Info()
            .title("Ieunmemo Command API Component")
            .version("1")
            .description(
                "<h3><a href=\"/command/swagger-ui.html\" style=\"text-decoration:none\">Command Swagger 바로가기 (Command API 모음)</a></h3>"
                    + "<h3><a href=\"/query/swagger-ui.html\" style=\"text-decoration:none\">Query Swagger 바로가기 (Query API 모음)</a></h3>"));
  }
}
