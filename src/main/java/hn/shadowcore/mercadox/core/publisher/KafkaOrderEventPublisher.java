package hn.shadowcore.mercadox.core.publisher;

import hn.shadowcore.mercadox.core.util.EmailDispatchUtils;
import hn.shadowcore.mercadox.library.entity.model.enums.kafka.KafkaTopic;
import hn.shadowcore.mercadox.library.entity.model.enums.kafka.publisher.EmailEventPublisher;
import hn.shadowcore.mercadox.library.entity.response.dto.EmailEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaOrderEventPublisher implements EmailEventPublisher {

    private final EmailDispatchUtils emailDispatchUtils;
    private static final String TOPIC_ORDER_PLACED = KafkaTopic.ORDER_PLACING;
    private static final String TOPIC_ORDER_DISPATCHED = KafkaTopic.ORDER_CONFIRMED;
    private static final String TOPIC_ORDER_CANCELLED = KafkaTopic.ORDER_CANCELLED;

    @Override
    public void publishOrderPlaced(EmailEventDto<?> event) {

    }

    @Override
    public void publishOrderDispatch(EmailEventDto<?> event) {

    }

    @Override
    public void publishOrderCancelled(EmailEventDto<?> event) {

    }
}
