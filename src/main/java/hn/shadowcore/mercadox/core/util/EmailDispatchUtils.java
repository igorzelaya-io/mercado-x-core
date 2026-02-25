package hn.shadowcore.mercadox.core.util;


import hn.shadowcore.mercadox.core.service.UserService;
import hn.shadowcore.mercadoxlibrary.entity.model.auth.User;
import hn.shadowcore.mercadoxlibrary.entity.response.dto.EmailRecipientDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmailDispatchUtils {

    private final UserService userService;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CompletableFuture<SendResult<String, Object>> sendEmail
            (ProducerRecord<String, Object> producerRecord) {
        return kafkaTemplate.send(producerRecord);
    }

    public List<EmailRecipientDto> mapOrgAdminsToEmailRecipient() {
        return userService.findAllEnabledOrgAdmins()
                .stream()
                .map(this::mapSingleRecipient)
                .collect(Collectors.toList());
    }

    public EmailRecipientDto mapSingleRecipient(User user) {
        return new EmailRecipientDto(user.getFirstName(), user.getEmail());
    }

}
