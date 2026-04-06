package com.example.sell_lp.service.user;


import com.example.sell_lp.dto.request.AuthenticationRequest;
import com.example.sell_lp.dto.request.UserChangePasswordRequest;
import com.example.sell_lp.entity.User;
import com.example.sell_lp.enums.Provider;
import com.example.sell_lp.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
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
        user.setProvider(Provider.LOCAL.name());

        userRepository.save(user);
    }
}
