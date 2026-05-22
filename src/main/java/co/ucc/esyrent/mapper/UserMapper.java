package co.ucc.esyrent.mapper;

import co.ucc.esyrent.domain.entity.User;
import co.ucc.esyrent.dto.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}
