package com.example.sell_lp.mapper;

import com.example.sell_lp.dto.request.UserCreationRequest;
import com.example.sell_lp.dto.response.UserResponse;
import com.example.sell_lp.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest userCreationRequest);
    UserResponse toUserResponse(User user);
}
