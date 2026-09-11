package hn.alturaforge.mercadox.core.publisher;

import hn.alturaforge.mercadox.context.utils.KafkaProducerRecordFactory;
import hn.alturaforge.mercadox.library.entity.kafka.KafkaTopic;
import hn.alturaforge.mercadox.library.entity.avro.LeadCreatedEvent;
import hn.alturaforge.mercadox.library.entity.kafka.publisher.LeadEventPublisher;
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
    public void publishLeadCreated(LeadCreatedEvent leadCreatedEvent) {

        ProducerRecord<String, Object> recordEvent = KafkaProducerRecordFactory
                .buildWithoutOrgIdHeader(TOPIC_LEAD_CREATED, leadCreatedEvent.getEventId(), leadCreatedEvent);

        kafkaTemplate.send(recordEvent);

    }
}
