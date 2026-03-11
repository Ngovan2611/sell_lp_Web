package com.example.sell_lp.service;


import com.example.sell_lp.dto.request.AuthenticationRequest;
import com.example.sell_lp.dto.request.UserCreationRequest;
import com.example.sell_lp.entity.User;
import com.example.sell_lp.mapper.UserMapper;
import com.example.sell_lp.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    public User login(AuthenticationRequest authenticationRequest) {

        User user = userRepository.findByUsername(authenticationRequest.getUsername());

        if(user == null){
            return null;
        }

        boolean isMatch = BCrypt.checkpw(authenticationRequest.getPassword(), user.getPassword());

        if(isMatch){
            return user;
        }

        return null;
    }
    public User createUser(UserCreationRequest userCreationRequest) {

        User existingUser = userRepository.findByUsername(userCreationRequest.getUsername());

        if(existingUser != null){
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        User user = userMapper.toUser(userCreationRequest);

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        sdf.format(now);
        String password = BCrypt.hashpw(userCreationRequest.getPassword(), BCrypt.gensalt(10));

        user.setPassword(password);
        user.setActive(true);
        user.setCreatedAt(now);
        return userRepository.save(user);
    }
}
