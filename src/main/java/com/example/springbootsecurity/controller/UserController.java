package com.example.springbootsecurity.controller;

import com.example.springbootsecurity.exception.ResourceNotFoundException;
import com.example.springbootsecurity.model.User;
import com.example.springbootsecurity.repo.UserRepository;
import com.example.springbootsecurity.response.ApiResponse;
import com.example.springbootsecurity.response.UserProfile;
import com.example.springbootsecurity.service.CurrentUser;
import com.example.springbootsecurity.service.RefreshTokenService;
import com.example.springbootsecurity.service.UserDeviceService;
import com.example.springbootsecurity.service.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/byId/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserProfile getUserProfileById(@PathVariable Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return new UserProfile(user.getId(), user.getEmail(), user.getName(), user.getActive());
    }

    @PutMapping("/byId/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deactivateUserById(@PathVariable Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.deactivate();
        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse(true, "User deactivated successfully!"));
    }


    @PutMapping("/byId/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> activateUserById(@PathVariable Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.activate();
        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse(true, "User activated successfully!"));
    }


}
