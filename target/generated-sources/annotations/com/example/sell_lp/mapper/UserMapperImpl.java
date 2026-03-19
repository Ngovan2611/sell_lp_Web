package com.example.sell_lp.mapper;

import com.example.sell_lp.dto.request.UserCreationRequest;
import com.example.sell_lp.dto.request.UserUpdateRequest;
import com.example.sell_lp.dto.response.UserResponse;
import com.example.sell_lp.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-19T13:38:38+0700",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Microsoft)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(UserCreationRequest userCreationRequest) {
        if ( userCreationRequest == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.username( userCreationRequest.getUsername() );
        user.password( userCreationRequest.getPassword() );
        user.fullName( userCreationRequest.getFullName() );

        return user.build();
    }

    @Override
    public void toUserUpdateRequest(User user, UserUpdateRequest userUpdateRequest) {
        if ( userUpdateRequest == null ) {
            return;
        }

        user.setFullName( userUpdateRequest.getFullName() );
        user.setEmail( userUpdateRequest.getEmail() );
        user.setPhone( userUpdateRequest.getPhone() );
    }

    @Override
    public UserResponse toUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.userId( user.getUserId() );
        userResponse.username( user.getUsername() );
        userResponse.fullName( user.getFullName() );
        userResponse.email( user.getEmail() );
        userResponse.phone( user.getPhone() );
        userResponse.createdAt( user.getCreatedAt() );
        userResponse.role( user.getRole() );
        userResponse.provider( user.getProvider() );

        return userResponse.build();
    }
}
