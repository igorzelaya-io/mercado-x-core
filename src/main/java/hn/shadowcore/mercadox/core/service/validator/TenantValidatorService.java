package hn.shadowcore.mercadox.core.service.validator;

import hn.shadowcore.mercadox.context.validator.AnonymousTenantValidator;
import hn.shadowcore.mercadox.library.entity.model.auth.Organization;
import hn.shadowcore.mercadox.library.jpa.repository.OrganizationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TenantValidatorService implements AnonymousTenantValidator {

    private final OrganizationRepository organizationRepository;
    @Override
    public boolean validate(String orgId) {
        return organizationRepository
                .findById(UUID.fromString(orgId))
                .map(Organization::getEnabled)
                .orElseThrow(() -> new EntityNotFoundException
                        ("Organization was not found or is not active."));
    }
}
