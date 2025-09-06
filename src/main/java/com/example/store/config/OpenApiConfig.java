package com.example.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;


/**
 * The type Open api config.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Custom open api.
     *
     * @return the open api
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(createApiInfo()).servers(createServers());
    }

    private Info createApiInfo() {
        return new Info()
                .title("Store API")
                .version("1.0")
                .description("A simple store management system")
                .contact(createContact())
                .license(createLicense())
                .termsOfService("https://www.securitease.com");
    }

    private Contact createContact() {
        return new Contact()
                .name("SecuritEase Dev")
                .url("https://www.securitease.com")
                .email("internal@securitease.com");
    }

    private License createLicense() {
        return new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.htm");
    }

    private List<Server> createServers() {
        return List.of(new Server().url("http://localhost:8080").description("Development server"));
    }
}
