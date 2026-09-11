package hn.alturaforge.mercadox.core.mapper;

import hn.alturaforge.mercadox.library.entity.model.core.Location;
import hn.alturaforge.mercadox.library.entity.response.dto.LocationDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDto toDto(Location location);

}
