package hn.shadowcore.mercadox.core.publisher;

import hn.shadowcore.mercadox.core.util.EmailDispatchUtils;
import hn.shadowcore.mercadoxlibrary.entity.model.enums.kafka.KafkaTopic;
import hn.shadowcore.mercadoxlibrary.entity.model.enums.kafka.publisher.EmailEventPublisher;
import hn.shadowcore.mercadoxlibrary.entity.response.EventDto;
import hn.shadowcore.mercadoxlibrary.entity.response.dto.EmailEventDto;
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
    public void publishOrderPlaced(EventDto<EmailEventDto<?>> eventDto) {

    }

    @Override
    public void publishOrderDispatch(EventDto<EmailEventDto<?>> eventDto) {

    }

    @Override
    public void publishOrderCancelled(EventDto<EmailEventDto<?>> eventDto) {

    }
}
