package hn.shadowcore.mercadox.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
@ComponentScan(basePackages =
        "hn.shadowcore.mercadoxcontext"
)
@EntityScan(basePackages = "hn.shadowcore.mercadoxlibrary.entity")
@EnableJpaRepositories(
        basePackages = "hn.shadowcore.mercadoxlibrary.jpa.repository"
)
public class MercadoxCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MercadoxCoreApplication.class, args);
    }

}


