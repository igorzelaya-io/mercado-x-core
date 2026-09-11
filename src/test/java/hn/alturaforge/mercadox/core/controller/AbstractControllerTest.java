package hn.alturaforge.mercadox.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hn.alturaforge.mercadox.context.security.JwtVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.interfaces.RSAPublicKey;

public abstract class AbstractControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected JwtVerifier jwtVerifier;

    @MockitoBean
    protected RSAPublicKey rsaPublicKey;

    public AbstractControllerTest() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
}
