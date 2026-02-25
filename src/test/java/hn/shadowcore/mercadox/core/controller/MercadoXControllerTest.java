package hn.shadowcore.mercadox.core.controller;

import hn.shadowcore.mercadox.core.MercadoxCoreApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = MercadoxCoreApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public @interface MercadoXControllerTest {
}