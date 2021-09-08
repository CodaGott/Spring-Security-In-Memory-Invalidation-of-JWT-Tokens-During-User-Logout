package com.example.springbootsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginForm {

    @NotBlank(message = "email field can't be blank")
    @Size(min = 3, max = 60)
    private String email;

    @NotBlank(message = "Password field can't be empty")
    @Size(min = 8, max = 30)
    private String password;

    @Valid
    @NotNull(message = "Device info cannot be null")
    private DeviceInfo deviceInfo;
}
