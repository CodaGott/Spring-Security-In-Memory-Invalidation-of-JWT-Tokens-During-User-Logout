package com.example.springbootsecurity.controller;

import com.example.springbootsecurity.dto.LoginForm;
import com.example.springbootsecurity.model.RefreshToken;
import com.example.springbootsecurity.model.User;
import com.example.springbootsecurity.model.UserDevice;
import com.example.springbootsecurity.repo.RoleRepository;
import com.example.springbootsecurity.repo.UserRepository;
import com.example.springbootsecurity.response.ApiResponse;
import com.example.springbootsecurity.response.JwtResponse;
import com.example.springbootsecurity.security.JwtProvider;
import com.example.springbootsecurity.service.RefreshTokenService;
import com.example.springbootsecurity.service.UserDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserDeviceService userDeviceService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User not found."));

        if (user.getActive()){
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwtToken = jwtProvider.generateJwtToken(authentication);
            userDeviceService.findByUserId(user.getId())
                    .map(UserDevice::getRefreshToken)
                    .map(RefreshToken::getId)
                    .ifPresent(refreshTokenService::deleteById);

            UserDevice userDevice = userDeviceService.createUserDevice(loginRequest.getDeviceInfo());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken();
            userDevice.setUser(user);
            userDevice.setRefreshToken(refreshToken);
            refreshToken.setUserDevice(userDevice);
            refreshToken = refreshTokenService.save(refreshToken);
            return ResponseEntity.ok(new JwtResponse(jwtToken, refreshToken.getToken(), jwtProvider.getExpiryDuration()));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "User has been deactivated/locked !!"));
    }

}
