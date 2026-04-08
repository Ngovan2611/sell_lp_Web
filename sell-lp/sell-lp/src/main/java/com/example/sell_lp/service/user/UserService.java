package com.example.sell_lp.service.user;


import com.example.sell_lp.dto.request.UserCreationRequest;
import com.example.sell_lp.dto.request.UserUpdateRequest;
import com.example.sell_lp.dto.response.UserResponse;
import com.example.sell_lp.entity.Role;
import com.example.sell_lp.entity.User;
import com.example.sell_lp.enums.Provider;
import com.example.sell_lp.mapper.UserMapper;
import com.example.sell_lp.repository.UserRepository;
import com.example.sell_lp.service.authorization.RoleService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Set;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserService {
    RoleService roleService;
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    UserMapper userMapper;



    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null){
            return null;
        }
        return userMapper.toUserResponse(user);
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

    @Transactional
    public void updateUser(String userId, UserUpdateRequest userUpdateRequest) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername);

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals(com.example.sell_lp.enums.Role.ADMIN.name()));

        if (!isAdmin && !currentUser.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền này");
        }

        User userToUpdate = userRepository.findByUserId(userId);
        if (userToUpdate == null) throw new RuntimeException("User not found");

        User existingEmailUser = userRepository.findByEmail(userUpdateRequest.getEmail());
        if (existingEmailUser != null && !existingEmailUser.getUserId().equals(userId)) {
            throw new RuntimeException("Email này đã được sử dụng bởi tài khoản khác");
        }

        userMapper.toUserUpdateRequest(userToUpdate, userUpdateRequest);
        userRepository.save(userToUpdate);
    }
    public List<UserResponse> getAllUsersByRoles(Role role) {
        List<User> users = userRepository.findByRoles(Set.of(role));
        return users.stream().map(userMapper::toUserResponse).toList();
    }
    @Transactional
    public void toggleUserStatus(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }


    public Long countUser() {
        return userRepository.count();
    }

}
