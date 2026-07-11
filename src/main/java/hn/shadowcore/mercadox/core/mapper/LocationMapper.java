package hn.shadowcore.mercadox.core.mapper;

import hn.shadowcore.mercadox.library.entity.model.core.Location;
import hn.shadowcore.mercadox.library.entity.response.dto.LocationDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDto toDto(Location location);

}
