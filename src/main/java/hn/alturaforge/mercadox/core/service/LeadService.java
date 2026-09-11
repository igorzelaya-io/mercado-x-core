package hn.alturaforge.mercadox.core.service;

import hn.alturaforge.mercadox.core.mapper.LeadMapper;
import hn.alturaforge.mercadox.core.publisher.KafkaLeadEventPublisher;
import hn.alturaforge.mercadox.library.entity.model.auth.Organization;
import hn.alturaforge.mercadox.library.entity.model.core.Lead;
import hn.alturaforge.mercadox.library.entity.avro.LeadCreatedEvent;
import hn.alturaforge.mercadox.library.entity.ports.incoming.ClientLeadUseCase;
import hn.alturaforge.mercadox.library.entity.request.ClientLeadRequest;
import hn.alturaforge.mercadox.library.jpa.repository.LeadRepository;
import hn.alturaforge.mercadox.library.jpa.repository.OrganizationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeadService implements ClientLeadUseCase {

    private final LeadRepository leadRepository;

    private final OrganizationRepository organizationRepository;

    private final LeadMapper leadMapper;

    private final KafkaLeadEventPublisher leadEventPublisher;

    @Override
    public void generateLead(ClientLeadRequest clientLeadRequest, String orgId) {

        if (StringUtils.hasText(clientLeadRequest.getWebsite())) {
            log.warn("Honeypot field populated for Organization: '{}'; silently dropping submission.", orgId);
            return;
        }

        final Organization organization = organizationRepository
                .findById(UUID.fromString(orgId))
                .orElseThrow(() -> new EntityNotFoundException
                        (String.format("Organization with ID: '%s' is inactive or non-existent", orgId)));

        final Lead lead = Lead
                .createNew(clientLeadRequest.getUserName(), clientLeadRequest.getOrgName(),
                        clientLeadRequest.getEmail(), clientLeadRequest.getPhoneNumber(),
                        clientLeadRequest.getMessage(), organization);

        final Lead savedLead = leadRepository.save(lead);

        log.info("New Lead Prospect was created for Organization: '{}'", organization.getName());

        LeadCreatedEvent leadEvent = leadMapper.toCreatedEvent(savedLead);

        leadEventPublisher.publishLeadCreated(leadEvent);
    }
}
