package hn.shadowcore.mercadox.core.publisher;

import hn.shadowcore.mercadox.context.utils.KafkaProducerRecordFactory;
import hn.shadowcore.mercadox.library.entity.model.enums.kafka.KafkaTopic;
import hn.shadowcore.mercadox.library.entity.model.enums.kafka.event.LeadCreatedEvent;
import hn.shadowcore.mercadox.library.entity.model.enums.kafka.publisher.LeadEventPublisher;
import hn.shadowcore.mercadox.library.entity.response.EventDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLeadEventPublisher implements LeadEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_LEAD_CREATED = KafkaTopic.LEAD_CREATED;

    @Override
    public void publishLeadCreated(EventDto<LeadCreatedEvent> eventDto) {

        ProducerRecord<String, Object> recordEvent = KafkaProducerRecordFactory
                .buildWithoutOrgIdHeader(TOPIC_LEAD_CREATED, eventDto.getEventId(), eventDto.getEventPayload());

        kafkaTemplate.send(recordEvent);

    }
}
