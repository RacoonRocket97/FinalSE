package com.lms.mappers;

import com.lms.dto.request.UserRequestDto;
import com.lms.dto.response.UserResponseDto;
import com.lms.models.Role;
import com.lms.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdCourses", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    User toEntity(UserRequestDto dto);

    @Mapping(target = "roles", qualifiedByName = "rolesToStringList")
    UserResponseDto toResponseDto(User user);

    List<UserResponseDto> toResponseDtoList(List<User> users);

    @Named("rolesToStringList")
    default List<String> rolesToStringList(List<Role> roles) {
        if (roles == null) return List.of();
        return roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
    }
}