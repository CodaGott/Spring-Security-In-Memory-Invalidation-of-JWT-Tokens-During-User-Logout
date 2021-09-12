package com.example.springbootsecurity.controller;

import com.example.springbootsecurity.exception.ResourceNotFoundException;
import com.example.springbootsecurity.model.User;
import com.example.springbootsecurity.repo.UserRepository;
import com.example.springbootsecurity.response.UserProfile;
import com.example.springbootsecurity.service.CurrentUser;
import com.example.springbootsecurity.service.RefreshTokenService;
import com.example.springbootsecurity.service.UserDeviceService;
import com.example.springbootsecurity.service.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserProfile> getUserProfile(@RequestParam(value = "email", required = false)Optional<String> email){
        List<UserProfile> userProfiles = new ArrayList<>();
        if (email.isPresent()){
            User user = userRepository.findByEmail(email.get())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", email.get()));
            UserProfile userProfile = new UserProfile(user.getId(), user.getEmail(), user.getName(), user.getActive());
            userProfiles.add(userProfile);
        }else {
            List<User> users = userRepository.findAll();
            for (User u : users){
                UserProfile userProfile = new UserProfile(u.getId(), u.getEmail(), u.getName(), u.getActive());
                userProfiles.add(userProfile);
            }
        }
        return userProfiles;
    }

}
