package com.example.springbootsecurity.service;

import com.example.springbootsecurity.dto.DeviceInfo;
import com.example.springbootsecurity.exception.TokenRefreshException;
import com.example.springbootsecurity.model.RefreshToken;
import com.example.springbootsecurity.model.UserDevice;
import com.example.springbootsecurity.repo.UserDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDeviceService {

    @Autowired
    private UserDeviceRepository userDeviceRepository;

    public Optional<UserDevice> findByUserId(Long userId) {
        return userDeviceRepository.findByUserId(userId);
    }

    public Optional<UserDevice> findByRefreshToken(RefreshToken refreshToken){
        return userDeviceRepository.findByRefreshToken(refreshToken);
    }

    public UserDevice createUserDevice(DeviceInfo deviceInfo){
        UserDevice userDevice = new UserDevice();
        userDevice.setDeviceId(deviceInfo.getDeviceId());
        userDevice.setDeviceType(deviceInfo.getDeviceType());
        userDevice.setIsRefreshActive(true);
        return userDevice;
    }

    public void verifyRefreshAvailability(RefreshToken refreshToken){
        UserDevice userDevice = findByRefreshToken(refreshToken)
                .orElseThrow(() -> new TokenRefreshException(refreshToken.getToken(), "No device found for the matching token. Please login again"));

        if (!userDevice.getIsRefreshActive()){
            throw new TokenRefreshException(refreshToken.getToken(), "Refresh blocked for the device. Please login through a different device");
        }
    }
}
