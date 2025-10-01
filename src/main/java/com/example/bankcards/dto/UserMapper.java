package com.example.bankcards.dto;

import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToStrings")
    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "cards", ignore = true)
    User toEntity(UserCreateRequest request);

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "cards", ignore = true)
    void updateUserFromDto(UserUpdateRequest request, @MappingTarget User user);

    @Named("mapRolesToStrings")
    default Set<String> mapRolesToStrings(Set<com.example.bankcards.entity.Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());
    }
}
