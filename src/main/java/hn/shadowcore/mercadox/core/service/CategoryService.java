package hn.shadowcore.mercadox.core.service;

import hn.shadowcore.mercadoxcontext.utils.OrgIdContextHolder;
import hn.shadowcore.mercadoxlibrary.entity.model.auth.Organization;
import hn.shadowcore.mercadoxlibrary.entity.model.core.Category;
import hn.shadowcore.mercadoxlibrary.entity.ports.incoming.CategoryUseCase;
import hn.shadowcore.mercadoxlibrary.jpa.repository.CategoryRepository;
import hn.shadowcore.mercadoxlibrary.jpa.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryUseCase {

    private final CategoryRepository categoryRepository;

    private final OrganizationRepository organizationRepository;
    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(String id) {
        return categoryRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Category was not found for ID:" + id));
    }

    @Override
    public Category saveCategory(Category category) {

        final String orgId = OrgIdContextHolder.getTenantId();

        Organization org = organizationRepository.findById(UUID.fromString(orgId))
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("Organization was not found for: '%s'", orgId)));

        Category entity = Category.builder()
                .id(UUID.randomUUID())
                .organization(org)
                .enabled(true)
                .build();

        return categoryRepository.save(entity);
    }

    @Override
    public void deleteCategoryById(String s) {
        categoryRepository.deleteById(UUID.fromString(s));
    }
}
