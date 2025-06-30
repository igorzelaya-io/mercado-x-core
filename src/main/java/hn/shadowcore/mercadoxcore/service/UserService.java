package hn.shadowcore.mercadoxcore.service;

import hn.shadowcore.mercadoxlibrary.entity.model.auth.QUser;
import hn.shadowcore.mercadoxlibrary.entity.model.auth.User;
import hn.shadowcore.mercadoxlibrary.jpa.predicate.UserPredicateFactory;
import hn.shadowcore.mercadoxlibrary.jpa.querydsl.OrgAwareQueryFactory;
import hn.shadowcore.mercadoxlibrary.jpa.repository.UserRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final OrgAwareQueryFactory orgAwareQueryFactory;

    private final UserPredicateFactory userPredicateFactory;

    private final UserRepository userRepository;

    public UserService(OrgAwareQueryFactory orgAwareQueryFactory, UserPredicateFactory userPredicateFactory,
                       UserRepository userRepository) {
        this.orgAwareQueryFactory = orgAwareQueryFactory;
        this.userPredicateFactory = userPredicateFactory;
        this.userRepository = userRepository;
    }

    public User findActiveUserByUsername(String username) {
        return Optional.ofNullable(
                orgAwareQueryFactory.selectFrom(QUser.user, QUser.user.orgId)
                        .where(QUser.user.username.eq(username)
                                .and(userPredicateFactory.isActive()))
                        .fetchOne()
        ).orElseThrow(() -> new ResourceNotFoundException
                (String.format("User not found for username: '%s'", username)));
    }

    public User findActiveUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User was not found for userId: '%s'", userId));
    }

    public User findActiveUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found for: '%s'", email));
    }

    public boolean existsById(String orgId) {
        return userRepository.existsById(UUID.fromString(orgId));
    }

}
