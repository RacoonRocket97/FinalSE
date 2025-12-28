package com.lms.mappers;

import com.lms.dto.request.UserRequestDto;
import com.lms.dto.response.UserResponseDto;
import com.lms.models.Role;
import com.lms.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",builder = @org.mapstruct.Builder(disableBuilder = true))
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdCourses", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    User toEntity(UserRequestDto dto);

    @Mapping(target = "roles", expression = "java(getRoleNames(user.getRoles()))")
    UserResponseDto toResponseDto(User user);

    List<UserResponseDto> toResponseDtoList(List<User> users);

    default List<String> getRoleNames(List<Role> roles) {
        return roles == null ? List.of() : roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
    }
}