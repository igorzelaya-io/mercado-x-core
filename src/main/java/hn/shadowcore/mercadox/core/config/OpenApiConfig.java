package hn.shadowcore.mercadox.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI mercadoXCoreOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("MercadoX Core API")
                        .description("Cart, orders, catalog, and organization endpoints")
                        .version("v1"));
    }

}
