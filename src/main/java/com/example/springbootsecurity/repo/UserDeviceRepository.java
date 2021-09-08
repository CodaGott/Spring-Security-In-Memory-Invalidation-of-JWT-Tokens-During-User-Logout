package com.example.springbootsecurity.repo;

import com.example.springbootsecurity.model.RefreshToken;
import com.example.springbootsecurity.model.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    Optional<UserDevice> findByRefreshToken(RefreshToken refreshToken);
    Optional<UserDevice> findByUserId(Long userId);
}
