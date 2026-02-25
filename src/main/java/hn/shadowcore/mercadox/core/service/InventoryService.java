package hn.shadowcore.mercadox.core.service;


import hn.shadowcore.mercadoxlibrary.entity.model.core.Inventory;
import hn.shadowcore.mercadoxlibrary.entity.model.core.Item;
import hn.shadowcore.mercadoxlibrary.jpa.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public Inventory saveInventory(Inventory inventory) {
        inventory.setId(UUID.randomUUID());
        return inventoryRepository.save(inventory);
    }

    public void addItemToInventory(String inventoryId, Item item) {
        Inventory inventory = inventoryRepository.findById(UUID.fromString(inventoryId))
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("Inventory was not found for ID: '%s'", inventoryId)));

        inventory.getItems().add(item);
        inventory.setQuantity(inventory.getQuantity() + 1);
        inventoryRepository.save(inventory);
    }

}
