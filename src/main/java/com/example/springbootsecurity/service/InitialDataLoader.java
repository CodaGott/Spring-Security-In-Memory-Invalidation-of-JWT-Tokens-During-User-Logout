package com.example.springbootsecurity.service;

import com.example.springbootsecurity.model.Role;
import com.example.springbootsecurity.model.RoleName;
import com.example.springbootsecurity.repo.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class InitialDataLoader {

    @Autowired
    private RoleRepository roleRepository;

    @Bean
    public ApplicationRunner initializer(){
        List<RoleName> roles = Arrays.asList(RoleName.ROLE_ADMIN, RoleName.ROLE_THERAPIST, RoleName.ROLE_USER);
        return args -> roles.forEach(i -> createRoleIfNotFound(i));
    }

    private Optional<Role> createRoleIfNotFound(RoleName roleName){
        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isEmpty()){
            Role newRole = new Role();
            newRole.setName(roleName);
            newRole = roleRepository.save(newRole);
        }
        return role;
    }
}
