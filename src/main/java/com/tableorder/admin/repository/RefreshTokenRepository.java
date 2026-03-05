package com.tableorder.admin.repository;

import com.tableorder.admin.domain.RefreshToken;
import com.tableorder.admin.domain.TokenRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true " +
           "WHERE rt.adminId = :adminId AND rt.revoked = false")
    void revokeAllByAdminId(Long adminId);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true " +
           "WHERE rt.tableId = :tableId AND rt.role = :role AND rt.revoked = false")
    void revokeAllByTableIdAndRole(Long tableId, TokenRole role);
}
