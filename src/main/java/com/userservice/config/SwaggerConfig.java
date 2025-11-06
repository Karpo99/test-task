package com.userservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String BEARER = "bearer";
    private static final String JWT = "JWT";
    private static final String BEARER_AUTH = "bearerAuth";
    private static final String PUBLIC_APIS = "public-apis";
    private static final String PATH = "/**";
    private static final String DESCRIPTION = "Spring security authentication demo";
    private static final String VERSION = "API:1";

    @Bean
    GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group(PUBLIC_APIS)
                .pathsToMatch(PATH)
                .build();
    }

    @Bean
    OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info().title(DESCRIPTION).version(VERSION))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(
                        new Components()
                                .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme(BEARER)
                                        .bearerFormat(JWT)));
    }
}
