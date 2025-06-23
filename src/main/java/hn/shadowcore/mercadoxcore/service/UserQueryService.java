package hn.shadowcore.mercadoxcore.service;


import hn.shadowcore.mercadoxlibrary.entity.model.auth.QUser;
import hn.shadowcore.mercadoxlibrary.entity.model.auth.User;
import hn.shadowcore.mercadoxlibrary.jpa.predicate.UserPredicateFactory;
import hn.shadowcore.mercadoxlibrary.jpa.querydsl.OrgAwareQueryFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserQueryService {

    private final OrgAwareQueryFactory orgAwareQueryFactory;

    private final UserPredicateFactory userPredicateFactory;

    public UserQueryService(OrgAwareQueryFactory orgAwareQueryFactory, UserPredicateFactory userPredicateFactory) {
        this.orgAwareQueryFactory = orgAwareQueryFactory;
        this.userPredicateFactory = userPredicateFactory;
    }

    public Optional<User> findActiveUserByUsername(String username) {
        return Optional.ofNullable(
                orgAwareQueryFactory.selectFrom(QUser.user, QUser.user.orgId)
                        .where(QUser.user.username.eq(username)
                                .and(userPredicateFactory.isActive()))
                        .fetchOne()
        );
    }

}
