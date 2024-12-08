package ru.andreyszdlv.taskmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.andreyszdlv.taskmanager.dto.auth.RegisterUserRequestDto;
import ru.andreyszdlv.taskmanager.dto.auth.RegisterUserResponseDto;
import ru.andreyszdlv.taskmanager.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    RegisterUserResponseDto toRegisterUserResponseDto(User user);

    User toUser(RegisterUserRequestDto registerUserRequestDto);

}
