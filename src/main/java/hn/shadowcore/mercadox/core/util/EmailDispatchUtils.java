package hn.shadowcore.mercadox.core.util;


import hn.shadowcore.mercadox.core.service.UserService;
import hn.shadowcore.mercadox.library.entity.model.auth.User;
import hn.shadowcore.mercadox.library.entity.model.auth.UserNotificationPreference;
import hn.shadowcore.mercadox.library.entity.model.enums.TemplateChannel;
import hn.shadowcore.mercadox.library.entity.response.dto.EmailRecipientDto;
import hn.shadowcore.mercadox.library.jpa.repository.UserNotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmailDispatchUtils {

    private final UserService userService;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final UserNotificationPreferenceRepository userNotificationPreferenceRepository;

    public CompletableFuture<SendResult<String, Object>> sendEmail
            (ProducerRecord<String, Object> producerRecord) {
        return kafkaTemplate.send(producerRecord);
    }

    public List<EmailRecipientDto> mapOrgAdminsToEmailRecipient() {
        return userService.findAllEnabledOrgAdmins()
                .stream()
                .filter(this::isEmailEnabled)
                .map(this::toRecipient)
                .collect(Collectors.toList());
    }

    public Optional<EmailRecipientDto> mapSingleRecipient(User user) {
        if (!isEmailEnabled(user)) {
            return Optional.empty();
        }
        return Optional.of(toRecipient(user));
    }

    private EmailRecipientDto toRecipient(User user) {
        return new EmailRecipientDto(user.getFirstName(), user.getEmail());
    }

    private boolean isEmailEnabled(User user) {
        return userNotificationPreferenceRepository
                .findByUserIdAndChannel(user.getId(), TemplateChannel.EMAIL)
                .map(UserNotificationPreference::getEnabled)
                .orElse(true);
    }

}
