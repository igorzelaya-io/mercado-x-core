package hn.alturaforge.mercadox.core.mapper;


import hn.alturaforge.mercadox.library.entity.model.auth.User;
import hn.alturaforge.mercadox.library.entity.response.dto.UserDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> userList);
}
