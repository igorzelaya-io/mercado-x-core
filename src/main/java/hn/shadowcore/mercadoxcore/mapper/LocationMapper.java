package hn.shadowcore.mercadoxcore.mapper;

import hn.shadowcore.mercadoxlibrary.entity.model.core.Location;
import hn.shadowcore.mercadoxlibrary.entity.response.dto.LocationDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDto toDto(Location location);

}
