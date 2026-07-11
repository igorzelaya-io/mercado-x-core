package hn.shadowcore.mercadox.core.mapper;


import hn.shadowcore.mercadox.library.entity.model.core.Lead;
import hn.shadowcore.mercadox.library.entity.model.enums.kafka.event.LeadCreatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeadMapper {

    @Mapping(source = "id", target="eventId")
    @Mapping(source = "organization.id", target = "orgId")
    LeadCreatedEvent toCreatedEvent(Lead lead);

}
