package ru.andreyszdlv.taskmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.andreyszdlv.taskmanager.dto.auth.RegisterUserRequestDto;
import ru.andreyszdlv.taskmanager.dto.auth.RegisterUserResponseDto;
import ru.andreyszdlv.taskmanager.dto.user.UserDto;
import ru.andreyszdlv.taskmanager.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    RegisterUserResponseDto toRegisterUserResponseDto(User user);

    @Mapping(target = "password", ignore = true)
    User toUser(RegisterUserRequestDto registerUserRequestDto);

    UserDto toUserDto(User user);
}
