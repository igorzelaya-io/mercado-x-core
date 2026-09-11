package hn.alturaforge.mercadox.core.controller;

import hn.alturaforge.mercadox.core.MercadoxCoreApplication;
import hn.alturaforge.mercadox.core.config.MercadoXCoreAuthConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MercadoXCoreAuthConfig.class)
@SpringBootTest(
        classes = MercadoxCoreApplication.class,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration" +
                        ",org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration" +
                        ",org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"
        }
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public @interface MercadoXControllerTest { }