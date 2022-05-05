package ru.javaops.topjava2.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//https://sabljakovich.medium.com/adding-basic-auth-authorization-option-to-openapi-swagger-documentation-java-spring-95abbede27e9
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
@OpenAPIDefinition(
        info = @Info(
                title = "REST API documentation",
                version = "1.0",
                description = "Приложение по <a href='https://javaops.ru/view/topjava2'>курсу TopJava-2</a> (решение выпускного проекта)",
                contact = @Contact(url = "https://javaops.ru/#contacts", name = "Grigory Kislin", email = "admin@javaops.ru")
        ),
        tags = {
                @Tag(name = "ProfileController", description = "default user profile api"),
                @Tag(name = "RestaurantController", description = "default user restaurants api"),
                @Tag(name = "DishController", description = "default user dishes api"),
                @Tag(name = "VoteController", description = "default user votes api"),
                @Tag(name = "AdminUserController", description = "admin users api"),
                @Tag(name = "AdminRestaurantController", description = "admin restaurants api"),
                @Tag(name = "AdminDishController", description = "admin dishes api"),
                @Tag(name = "AdminVoteController", description = "admin votes api")
        },
        security = @SecurityRequirement(name = "basicAuth")
)
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("REST API")
                .pathsToMatch("/api/**")
                .build();
    }
}
