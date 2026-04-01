package com.example.sell_lp.service;


import com.example.sell_lp.entity.Role;
import com.example.sell_lp.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;

    public Role getByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }
}
