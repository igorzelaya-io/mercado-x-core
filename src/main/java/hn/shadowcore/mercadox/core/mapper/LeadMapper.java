package hn.shadowcore.mercadox.core.mapper;

import hn.shadowcore.mercadox.library.entity.avro.LeadCreatedEvent;
import hn.shadowcore.mercadox.library.entity.model.core.Lead;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeadMapper {

    @Mapping(source = "id",              target = "eventId")
    @Mapping(source = "organization.id", target = "orgId")
    @Mapping(source = "orgName",         target = "orgName")
    @Mapping(source = "userName",        target = "userName")
    @Mapping(source = "email",           target = "email")
    @Mapping(source = "phoneNumber",     target = "phoneNumber")
    @Mapping(target = "eventType",       ignore = true)
    @Mapping(target = "occurredAt",      ignore = true)
    LeadCreatedEvent toCreatedEvent(Lead lead);

}
