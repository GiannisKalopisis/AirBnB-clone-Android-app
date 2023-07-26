package com.dit.airbnb.service;

import com.dit.airbnb.dto.Role;
import com.dit.airbnb.dto.enums.RoleName;
import com.dit.airbnb.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role saveRole(RoleName roleName) {
        Role searchedRole = roleRepository.findByName(roleName).orElse(null);
        if (searchedRole != null) return searchedRole;
        return roleRepository.save(Role.builder().name(roleName).build());
    }

    public Optional<Role> findByName(RoleName roleName) {
        return roleRepository.findByName(roleName);
    }

}
