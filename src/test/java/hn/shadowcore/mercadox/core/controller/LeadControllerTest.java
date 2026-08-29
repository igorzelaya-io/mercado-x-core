package hn.shadowcore.mercadox.core.controller;

import hn.shadowcore.mercadox.context.validator.AnonymousTenantValidator;
import hn.shadowcore.mercadox.core.utils.mother.ClientLeadRequestMother;
import hn.shadowcore.mercadox.library.entity.model.auth.Organization;
import hn.shadowcore.mercadox.library.entity.ports.incoming.ClientLeadUseCase;
import hn.shadowcore.mercadox.library.entity.request.ClientLeadRequest;
import hn.shadowcore.mercadox.library.jpa.repository.OrganizationRepository;
import hn.shadowcore.mercadox.library.redis.util.RedisRateLimiter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MercadoXControllerTest
class LeadControllerTest extends AbstractControllerTest {

    @MockitoBean
    private AnonymousTenantValidator tenantValidatorService;

    @MockitoBean
    private OrganizationRepository organizationRepository;

    @MockitoBean
    private ClientLeadUseCase clientLeadUseCase;

    @MockitoBean
    private RedisRateLimiter redisRateLimiter;

    @BeforeEach
    void allowAllByDefault() {
        when(redisRateLimiter.tryConsume(anyString(), anyInt(), any(Duration.class))).thenReturn(true);
    }

    @Test
    void publicEndpointShouldWorkWithoutJwt() throws Exception {
        final String orgId = UUID.randomUUID().toString();

        doNothing().when(clientLeadUseCase).generateLead(any(ClientLeadRequest.class), any(String.class));

        when(tenantValidatorService.validate(anyString())).thenReturn(true);

        when(organizationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(Organization.buildStaticTestOrg()));

        mockMvc.perform(post("/api/v1/public/orgs/{orgId}/leads", orgId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ClientLeadRequestMother.valid())))
                .andExpect(status().isCreated());

        verify(tenantValidatorService).validate(orgId);
    }

    @Test
    void shouldRejectOversizedUserName() throws Exception {
        final String orgId = UUID.randomUUID().toString();

        when(tenantValidatorService.validate(anyString())).thenReturn(true);
        when(organizationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(Organization.buildStaticTestOrg()));

        mockMvc.perform(post("/api/v1/public/orgs/{orgId}/leads", orgId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ClientLeadRequestMother.withOversizedUserName())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectMalformedEmail() throws Exception {
        final String orgId = UUID.randomUUID().toString();

        when(tenantValidatorService.validate(anyString())).thenReturn(true);
        when(organizationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(Organization.buildStaticTestOrg()));

        mockMvc.perform(post("/api/v1/public/orgs/{orgId}/leads", orgId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ClientLeadRequestMother.withMalformedEmail())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectMalformedPhone() throws Exception {
        final String orgId = UUID.randomUUID().toString();

        when(tenantValidatorService.validate(anyString())).thenReturn(true);
        when(organizationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(Organization.buildStaticTestOrg()));

        mockMvc.perform(post("/api/v1/public/orgs/{orgId}/leads", orgId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ClientLeadRequestMother.withMalformedPhone())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectOversizedMessage() throws Exception {
        final String orgId = UUID.randomUUID().toString();

        when(tenantValidatorService.validate(anyString())).thenReturn(true);
        when(organizationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(Organization.buildStaticTestOrg()));

        mockMvc.perform(post("/api/v1/public/orgs/{orgId}/leads", orgId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ClientLeadRequestMother.withOversizedMessage())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenOrganizationNotFound() throws Exception {
        final String orgId = UUID.randomUUID().toString();

        when(tenantValidatorService.validate(anyString())).thenReturn(true);
        when(organizationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(Organization.buildStaticTestOrg()));
        doThrow(new EntityNotFoundException("Organization not found"))
                .when(clientLeadUseCase).generateLead(any(ClientLeadRequest.class), any(String.class));

        mockMvc.perform(post("/api/v1/public/orgs/{orgId}/leads", orgId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ClientLeadRequestMother.valid())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnTooManyRequestsWhenIpRateLimitExceeded() throws Exception {
        final String orgId = UUID.randomUUID().toString();

        when(redisRateLimiter.tryConsume(anyString(), anyInt(), any(Duration.class))).thenReturn(false);

        mockMvc.perform(post("/api/v1/public/orgs/{orgId}/leads", orgId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ClientLeadRequestMother.valid())))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void shouldReturnPayloadTooLargeWhenBodyExceedsCap() throws Exception {
        final String orgId = UUID.randomUUID().toString();

        ClientLeadRequest oversized = ClientLeadRequestMother.valid();
        String hugeBody = objectMapper.writeValueAsString(oversized)
                .replace("Hello world!", "A".repeat(9000));

        mockMvc.perform(post("/api/v1/public/orgs/{orgId}/leads", orgId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(hugeBody))
                .andExpect(status().isPayloadTooLarge());
    }
}
