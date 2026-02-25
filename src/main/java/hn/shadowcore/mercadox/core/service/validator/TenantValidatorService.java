package hn.shadowcore.mercadox.core.service.validator;

import hn.shadowcore.mercadoxcontext.validator.AnonymousTenantValidator;
import hn.shadowcore.mercadoxlibrary.entity.model.auth.Organization;
import hn.shadowcore.mercadoxlibrary.jpa.repository.OrganizationRepository;
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
                .orElseThrow(() -> new RuntimeException("Organization was not found or is not active."));
    }
}
