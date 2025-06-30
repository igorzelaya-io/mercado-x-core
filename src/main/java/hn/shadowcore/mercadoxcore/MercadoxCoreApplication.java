package hn.shadowcore.mercadoxcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "hn.shadowcore.mercadoxoauth",
        "hn.shadowcore.mercadoxlibrary"
})
@EntityScan(basePackages = "hn.shadowcore.mercadoxlibrary.entity.model")
public class MercadoxCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MercadoxCoreApplication.class, args);
    }

}
