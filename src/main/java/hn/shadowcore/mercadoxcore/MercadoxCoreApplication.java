package hn.shadowcore.mercadoxcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
@ComponentScan(basePackages={
        "hn.shadowcore.mercadoxoauth"
})
@EntityScan(basePackages = "hn.shadowcore.mercadoxlibrary")
public class MercadoxCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MercadoxCoreApplication.class, args);
    }

}
