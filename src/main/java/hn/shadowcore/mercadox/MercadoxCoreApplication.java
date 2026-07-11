package hn.shadowcore.mercadox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@SpringBootApplication
public class MercadoxCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MercadoxCoreApplication.class, args);
    }

}
