package com.hiretrack.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hireTrackOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("HireTrack API")
                        .description("REST API for tracking job applications, status pipeline, follow-ups, and hiring stats.")
                        .version("1.0.0")
                        .contact(new Contact().name("Dumidu Lakshan").email("dumi.dev.lk@gmail.com")))
                .servers(List.of(new Server().url("http://localhost:8080").description("Local")));
    }

    @Bean
    public GroupedOpenApi hireTrackGroup() {
        return GroupedOpenApi.builder()
                .group("hiretrack")
                .pathsToMatch("/api/**")
                .build();
    }
}