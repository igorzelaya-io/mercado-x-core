package hn.alturaforge.mercadox.core.mapper;

import hn.alturaforge.mercadox.library.entity.model.core.Item;
import hn.alturaforge.mercadox.library.entity.response.dto.ItemDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDto toDto(Item item);

    Item toEntity(ItemDto itemDto);

}
