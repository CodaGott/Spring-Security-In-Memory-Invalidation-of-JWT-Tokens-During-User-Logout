package com.example.springbootsecurity.controller;

import com.example.springbootsecurity.exception.ResourceNotFoundException;
import com.example.springbootsecurity.model.User;
import com.example.springbootsecurity.repo.UserRepository;
import com.example.springbootsecurity.service.CurrentUser;
import com.example.springbootsecurity.service.RefreshTokenService;
import com.example.springbootsecurity.service.UserDeviceService;
import com.example.springbootsecurity.service.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserDeviceService userDeviceService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @GetMapping("/me")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal){
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }

}
