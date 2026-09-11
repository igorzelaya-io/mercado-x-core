package hn.alturaforge.mercadox.core.service;


import hn.alturaforge.mercadox.core.mapper.LeadMapper;
import hn.alturaforge.mercadox.core.publisher.KafkaLeadEventPublisher;
import hn.alturaforge.mercadox.core.utils.mother.ClientLeadRequestMother;
import hn.alturaforge.mercadox.library.entity.model.auth.Organization;
import hn.alturaforge.mercadox.library.entity.model.core.Lead;
import hn.alturaforge.mercadox.library.entity.avro.LeadCreatedEvent;
import hn.alturaforge.mercadox.library.entity.request.ClientLeadRequest;
import hn.alturaforge.mercadox.library.jpa.repository.LeadRepository;
import hn.alturaforge.mercadox.library.jpa.repository.OrganizationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private LeadMapper leadMapper;

    @Mock
    private KafkaLeadEventPublisher leadEventPublisher;

    @InjectMocks
    private LeadService leadService;

    @Test
    void shouldGenerateLeadSuccessfully() {

        String orgId = UUID.randomUUID().toString();
        Organization organization = Organization.buildStaticTestOrg();
        ClientLeadRequest request = ClientLeadRequestMother.valid();

        LeadCreatedEvent leadCreatedEvent = new LeadCreatedEvent();
        leadCreatedEvent.setEventId(UUID.randomUUID().toString());

        when(organizationRepository.findById(eq(UUID.fromString(orgId))))
                .thenReturn(Optional.of(organization));

        when(leadRepository.save(any(Lead.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(leadMapper.toCreatedEvent(any(Lead.class)))
                .thenReturn(leadCreatedEvent);

        leadService.generateLead(request, orgId);

        ArgumentCaptor<Lead> leadCaptor = ArgumentCaptor.forClass(Lead.class);
        verify(leadRepository).save(leadCaptor.capture());
        assertNotNull(leadCaptor.getValue().getId());
        assertEquals(request.getUserName(), leadCaptor.getValue().getUserName());
        assertEquals(request.getEmail(), leadCaptor.getValue().getEmail());

        verify(leadMapper).toCreatedEvent(any(Lead.class));

        ArgumentCaptor<LeadCreatedEvent> eventCaptor = ArgumentCaptor.forClass(LeadCreatedEvent.class);
        verify(leadEventPublisher).publishLeadCreated(eventCaptor.capture());
        assertNotNull(eventCaptor.getValue().getEventId());
    }

    @Test
    void shouldThrowExceptionWhenOrganizationNotFound() {

        // Arrange
        String orgId = UUID.randomUUID().toString();

        when(organizationRepository.findById(any()))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(EntityNotFoundException.class,
                () -> leadService.generateLead(
                        ClientLeadRequestMother.valid(),
                        orgId
                ));

        verifyNoInteractions(leadRepository);
        verifyNoInteractions(leadEventPublisher);
    }

    @Test
    void shouldSilentlyDropSubmissionWhenHoneypotFilled() {

        String orgId = UUID.randomUUID().toString();
        ClientLeadRequest request = ClientLeadRequestMother.withHoneypotFilled();

        leadService.generateLead(request, orgId);

        verifyNoInteractions(organizationRepository);
        verifyNoInteractions(leadRepository);
        verifyNoInteractions(leadEventPublisher);
    }

}
