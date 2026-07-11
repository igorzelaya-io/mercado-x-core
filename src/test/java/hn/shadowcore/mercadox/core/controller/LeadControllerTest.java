package hn.shadowcore.mercadox.core.controller;

import hn.shadowcore.mercadox.context.validator.AnonymousTenantValidator;
import hn.shadowcore.mercadox.core.utils.mother.ClientLeadRequestMother;
import hn.shadowcore.mercadox.library.entity.model.auth.Organization;
import hn.shadowcore.mercadox.library.entity.ports.incoming.ClientLeadUseCase;
import hn.shadowcore.mercadox.library.entity.request.ClientLeadRequest;
import hn.shadowcore.mercadox.library.jpa.repository.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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
}