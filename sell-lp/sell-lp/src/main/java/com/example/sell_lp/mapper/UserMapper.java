package com.example.sell_lp.mapper;

import com.example.sell_lp.dto.request.user.UserCreationRequest;
import com.example.sell_lp.dto.request.user.UserUpdateRequest;
import com.example.sell_lp.dto.response.user.UserResponse;
import com.example.sell_lp.entity.Role;
import com.example.sell_lp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest userCreationRequest);

    void toUserUpdateRequest(@MappingTarget User user, UserUpdateRequest userUpdateRequest);
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRoles")
    UserResponse toUserResponse(User user);
    @Named("mapRoles")
    default Set<String> mapRoles(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());
    }
}
