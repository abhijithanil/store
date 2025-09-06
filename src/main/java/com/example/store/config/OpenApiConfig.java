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
 * Configuration class for OpenAPI/Swagger documentation. This class configures the OpenAPI specification for the store
 * application.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates and configures the OpenAPI bean with application information.
     *
     * @return configured OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(createApiInfo()).servers(createServers());
    }

    /**
     * Creates the API information section of the OpenAPI specification.
     *
     * @return Info object with application details
     */
    private Info createApiInfo() {
        return new Info()
                .title("Store API")
                .version("1.0")
                .description("A simple store management system")
                .contact(createContact())
                .license(createLicense())
                .termsOfService("https://www.securitease.com");
    }

    /**
     * Creates the contact information for the API.
     *
     * @return Contact object with developer details
     */
    private Contact createContact() {
        return new Contact()
                .name("SecuritEase Dev")
                .url("https://www.securitease.com")
                .email("internal@securitease.com");
    }

    /**
     * Creates the license information for the API.
     *
     * @return License object with license details
     */
    private License createLicense() {
        return new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.htm");
    }

    /**
     * Creates the server configurations for the API.
     *
     * @return List of Server objects
     */
    private List<Server> createServers() {
        return List.of(new Server().url("http://localhost:8080").description("Development server"));
    }
}
