package hn.shadowcore.mercadox.core.service;

import hn.shadowcore.mercadox.core.mapper.LeadMapper;
import hn.shadowcore.mercadox.core.publisher.KafkaLeadEventPublisher;
import hn.shadowcore.mercadoxlibrary.entity.model.core.Lead;
import hn.shadowcore.mercadoxlibrary.entity.model.enums.kafka.event.LeadCreatedEvent;
import hn.shadowcore.mercadoxlibrary.entity.ports.incoming.ClientLeadUseCase;
import hn.shadowcore.mercadoxlibrary.entity.request.ClientLeadRequest;
import hn.shadowcore.mercadoxlibrary.entity.response.EventDto;
import hn.shadowcore.mercadoxlibrary.jpa.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeadService implements ClientLeadUseCase {

    private final LeadRepository leadRepository;

    private final LeadMapper leadMapper;

    private final KafkaLeadEventPublisher leadEventPublisher;

    @Override
    public void generateLead(ClientLeadRequest clientLeadRequest) {

        final Lead lead = Lead
                .createNew(clientLeadRequest.getUserName(), clientLeadRequest.getOrgName(),
                        clientLeadRequest.getEmail(), clientLeadRequest.getPhoneNumber(),
                        clientLeadRequest.getMessage());

        leadRepository.save(lead);

        LeadCreatedEvent leadEvent = leadMapper.toCreatedEvent(lead);

        EventDto<LeadCreatedEvent> kafkaEvent = EventDto.<LeadCreatedEvent>builder()
                .eventId(leadEvent.getEventId())
                .eventPayload(leadEvent)
                .build();

        leadEventPublisher.publishLeadCreated(kafkaEvent);
    }
}
