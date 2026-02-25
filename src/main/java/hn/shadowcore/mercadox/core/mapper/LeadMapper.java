package hn.shadowcore.mercadox.core.mapper;


import hn.shadowcore.mercadoxlibrary.entity.model.core.Lead;
import hn.shadowcore.mercadoxlibrary.entity.model.enums.kafka.event.LeadCreatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeadMapper {

    @Mapping(source = "id", target="eventId")
    LeadCreatedEvent toCreatedEvent(Lead lead);

}
