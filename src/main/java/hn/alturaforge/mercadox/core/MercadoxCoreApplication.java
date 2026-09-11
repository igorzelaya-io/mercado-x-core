package hn.alturaforge.mercadox.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@SpringBootApplication(scanBasePackages = {
        "hn.alturaforge.mercadox.core",
        "hn.alturaforge.mercadox.library",
        "hn.alturaforge.mercadox.context"
})
public class MercadoxCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MercadoxCoreApplication.class, args);
    }

}
