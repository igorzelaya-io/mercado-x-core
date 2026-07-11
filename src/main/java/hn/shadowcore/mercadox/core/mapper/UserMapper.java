package hn.shadowcore.mercadox.core.mapper;


import hn.shadowcore.mercadox.library.entity.model.auth.User;
import hn.shadowcore.mercadox.library.entity.response.dto.UserDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> userList);
}
