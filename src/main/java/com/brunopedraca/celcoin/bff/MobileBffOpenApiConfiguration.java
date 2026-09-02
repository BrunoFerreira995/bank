package com.brunopedraca.celcoin.bff;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "mobile.bff", name = "enabled", havingValue = "true")
public class MobileBffOpenApiConfiguration {
    @Bean
    GroupedOpenApi mobileV1OpenApi() {
        return GroupedOpenApi.builder()
                .group("mobile-v1")
                .pathsToMatch("/mobile/v1/**")
                .addOpenApiCustomizer(openApi ->
                        openApi.info(new Info().title("Mobile BFF API").version("v1")))
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.addParametersItem(new HeaderParameter()
                            .name("X-Correlation-Id")
                            .description("Optional request correlation identifier; generated when absent.")
                            .schema(new StringSchema()));
                    return operation;
                })
                .build();
    }

    @Bean
    OpenAPI mobileBffApiInfo() {
        return new OpenAPI().info(new Info().title("Mobile BFF API").version("v1"));
    }
}
