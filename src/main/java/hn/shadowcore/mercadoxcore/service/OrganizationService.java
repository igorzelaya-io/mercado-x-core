package hn.shadowcore.mercadoxcore.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import hn.shadowcore.mercadoxlibrary.entity.model.auth.Organization;
import hn.shadowcore.mercadoxlibrary.entity.model.auth.QOrganization;
import hn.shadowcore.mercadoxlibrary.entity.model.auth.User;
import hn.shadowcore.mercadoxlibrary.jpa.predicate.OrgPredicateFactory;
import hn.shadowcore.mercadoxlibrary.jpa.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final JPAQueryFactory jpaQueryFactory;

    private final OrganizationRepository orgRepository;

    private final UserService userService;

    private final OrgPredicateFactory orgPredicateFactory;
    private static final QOrganization qOrganization = QOrganization.organization;

    @Cacheable(value = "activeOrgs", key = "#orgId")
    public Organization findActiveOrgById(UUID orgId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(qOrganization)
                .where(orgPredicateFactory.isActive().and(qOrganization.id.eq(orgId)))
                .fetchOne())
                .orElseThrow(() -> new ResourceNotFoundException
                        (String.format("Organization was not found for ID: %s", orgId.toString())));
    }

    @Cacheable(value = "inactiveOrgs", key = "#orgId")
    public Organization findInactiveOrgById(UUID orgId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(QOrganization.organization)
                .where(orgPredicateFactory.isInactive().and(qOrganization.id.eq(orgId)))
                .fetchOne())
                .orElseThrow(() -> new ResourceNotFoundException
                        (String.format("Organization was not found for ID: %s", orgId.toString())));
    }


    @Cacheable(value = "activeOrgs", key = "#orgName")
    public Organization findByNameContaining(String orgName) {
        return orgRepository.findByNameContainingIgnoreCase(orgName)
                .orElseThrow(() -> new ResourceNotFoundException
                        (String.format("Org was not found for name: %s", orgName)));
    }

    public Organization createOrganization(Organization organization, Optional<String> creatorId) {
        
        Optional<User> superUser = creatorId
                .filter(userService::existsById)
                .map(userService::findActiveUserById);
        
        Organization org = organization.toBuilder()
                .enabled(true)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .orgBranches(new ArrayList<>())
                .services(new ArrayList<>())
                .items(new ArrayList<>())
                .build();

        creatorId.ifPresent(org::setUserAdminId);
        superUser.ifPresent(user -> org.setOrgUsers(List.of(user)));
        return orgRepository.save(org);
    }

}
