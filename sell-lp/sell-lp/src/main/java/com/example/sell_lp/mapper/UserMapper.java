package com.example.sell_lp.mapper;

import com.example.sell_lp.dto.request.UserCreationRequest;
import com.example.sell_lp.dto.request.UserUpdateRequest;
import com.example.sell_lp.dto.response.UserResponse;
import com.example.sell_lp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest userCreationRequest);

    void toUserUpdateRequest(@MappingTarget User user, UserUpdateRequest userUpdateRequest);

    UserResponse toUserResponse(User user);
}
