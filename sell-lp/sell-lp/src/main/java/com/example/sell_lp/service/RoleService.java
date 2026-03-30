package com.example.sell_lp.service;


import com.example.sell_lp.entity.Role;
import com.example.sell_lp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role getByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

}
