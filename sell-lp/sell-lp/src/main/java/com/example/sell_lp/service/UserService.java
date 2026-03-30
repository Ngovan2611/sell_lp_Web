package com.example.sell_lp.service;


import com.example.sell_lp.dto.request.AuthenticationRequest;
import com.example.sell_lp.dto.request.UserChangePasswordRequest;
import com.example.sell_lp.dto.request.UserCreationRequest;
import com.example.sell_lp.dto.request.UserUpdateRequest;
import com.example.sell_lp.dto.response.UserResponse;
import com.example.sell_lp.entity.Role;
import com.example.sell_lp.entity.User;
import com.example.sell_lp.enums.Provider;
import com.example.sell_lp.mapper.UserMapper;
import com.example.sell_lp.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Set;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserService {
    RoleService roleService;
    PasswordEncoder passwordEncoder;

    UserRepository userRepository;

    UserMapper userMapper;


    public User login(AuthenticationRequest authenticationRequest) {

        User user = userRepository.findByUsername(authenticationRequest.getUsername());

        if(user == null){
            return null;
        }

        boolean isMatch = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());

        if(isMatch){
            return user;
        }

        return null;
    }

    public void createUser(UserCreationRequest userCreationRequest) {

        User existingUser = userRepository.findByUsername(userCreationRequest.getUsername());

        if(existingUser != null){
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        User user = userMapper.toUser(userCreationRequest);

        Date now = new Date();
        String password = passwordEncoder.encode(userCreationRequest.getPassword());
        Role defaultRole = roleService.getByRoleName(com.example.sell_lp.enums.Role.USER.name());
        user.setRoles(Set.of(defaultRole));
        user.setPassword(password);
        user.setActive(true);

        user.setCreatedAt(now);
        user.setProvider(Provider.LOCAL.name());
        userRepository.save(user);
    }


    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return userMapper.toUserResponse(user);

    }
    public User getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void updateUser(String userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findByUserId(userId);
        var existingUser = userRepository.findByEmail(userUpdateRequest.getEmail());
        if(existingUser != null && !existingUser.getUserId().equals(userId)){
            throw new RuntimeException("Email đã tồn tại");
        }
        userMapper.toUserUpdateRequest(user, userUpdateRequest);

        userRepository.save(user);
    }
    public void updatePassword(UserChangePasswordRequest request, String username) {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            throw new RuntimeException("User not found");
        }
        if (Provider.LOCAL.name().equals(user.getProvider())) {
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()) || request.getOldPassword().isEmpty()) {
                throw new RuntimeException("Mật khẩu cũ không đúng");
            }
        }

        if (user.getPassword() != null &&
                passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu mới không được trùng với mật khẩu cũ");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setProvider("LOCAL");

        userRepository.save(user);
    }

}
