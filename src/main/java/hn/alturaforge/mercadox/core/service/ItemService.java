package hn.alturaforge.mercadox.core.service;

import hn.alturaforge.mercadox.core.mapper.ItemMapper;
import hn.alturaforge.mercadox.context.utils.OrgIdContextHolder;
import hn.alturaforge.mercadox.library.entity.model.auth.Organization;
import hn.alturaforge.mercadox.library.entity.model.core.Category;
import hn.alturaforge.mercadox.library.entity.model.core.Inventory;
import hn.alturaforge.mercadox.library.entity.model.core.Item;
import hn.alturaforge.mercadox.library.entity.ports.incoming.ItemUseCase;
import hn.alturaforge.mercadox.library.entity.response.dto.ItemDto;
import hn.alturaforge.mercadox.library.jpa.repository.ItemRepository;
import hn.alturaforge.mercadox.library.jpa.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService implements ItemUseCase {

    private final OrganizationRepository organizationRepository;

    private final ItemRepository itemRepository;

    private final InventoryService inventoryService;

    private final CategoryService categoryService;

    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(ItemDto itemDto) {

        final String orgId = OrgIdContextHolder.getTenantId();

        final Organization organization = organizationRepository
                .findById(UUID.fromString(orgId))
                .orElseThrow(() -> new ResourceNotFoundException
                        (String.format("Organization was not found for ID: '%s'", orgId)));

        final Category category = categoryService.findById(itemDto.getCategoryId());

        Item itemEntity = itemMapper.toEntity(itemDto)
                .toBuilder()
                .id(UUID.randomUUID())
                .organization(organization)
                .category(category)
                .inStock(true)
                .build();

        itemRepository.save(itemEntity);
        inventoryService.addItemToInventory(itemDto.getInventoryId(), itemEntity);
        //TODO: check for image url and parse.
        return itemMapper.toDto(itemEntity);
    }

    @Override
    public Item updateItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public List<ItemDto> getItems() {
        return itemRepository.findAll()
                .stream()
                .map(itemMapper::toDto).toList();
    }

    @Override
    public Item getItemDetails(String s) {
        return itemRepository.findById(UUID.fromString(s))
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("Item was not found for id: '%s'", s)));
    }

    @Transactional
    public Item getItemDetailsForUpdate(String id) {
        return itemRepository.findByIdForUpdate(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("Item was not found for id: '%s'", id)));
    }

    @Override
    public void deleteItem(String id) {
        itemRepository.deleteById(UUID.fromString(id));
    }


    @Override
    public boolean isInStock(String id, int quantity) {
        return getItemInventory(id).getItems()
                .stream()
                .filter(i -> i.getId().toString().equals(id))
                .anyMatch(i -> i.getInStock() && i.getUnitQuantity() >= quantity);
    }

    @Override
    public Inventory getItemInventory(String s) {
        return itemRepository.findItemInventory(s);
    }
}
