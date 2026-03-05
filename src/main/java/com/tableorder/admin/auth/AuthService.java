package com.tableorder.admin.auth;

import com.tableorder.admin.auth.dto.AdminLoginRequest;
import com.tableorder.admin.auth.dto.AdminRegisterRequest;
import com.tableorder.admin.auth.dto.AdminRegisterResponse;
import com.tableorder.admin.auth.dto.TokenResponse;
import com.tableorder.admin.common.exception.BusinessException;
import com.tableorder.admin.common.exception.ErrorCode;
import com.tableorder.admin.domain.AdminAccount;
import com.tableorder.admin.domain.RefreshToken;
import com.tableorder.admin.domain.TokenRole;
import com.tableorder.admin.repository.AdminAccountRepository;
import com.tableorder.admin.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 15;

    private final AdminAccountRepository adminAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AdminRegisterResponse register(AdminRegisterRequest request) {
        if (adminAccountRepository.existsByStoreIdAndUsername(request.getStoreId(), request.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_DUPLICATE);
        }

        AdminAccount account = AdminAccount.create(
                request.getStoreId(),
                request.getUsername(),
                passwordEncoder.encode(request.getPassword())
        );
        account = adminAccountRepository.save(account);

        return new AdminRegisterResponse(account.getId(), account.getStoreId(), account.getUsername());
    }

    @Transactional
    public TokenResponse login(AdminLoginRequest request) {
        AdminAccount account = adminAccountRepository
                .findByStoreIdAndUsername(request.getStoreId(), request.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (account.isCurrentlyLocked()) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED, Map.of(
                    "lockUntil", account.getLockUntil(),
                    "remainingMinutes", java.time.Duration.between(
                            LocalDateTime.now(), account.getLockUntil()).toMinutes()
            ));
        }

        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            account.recordFailedAttempt(MAX_FAILED_ATTEMPTS, LOCK_DURATION_MINUTES);
            adminAccountRepository.save(account);
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, Map.of(
                    "remainingAttempts", MAX_FAILED_ATTEMPTS - account.getFailedAttempts()
            ));
        }

        account.resetFailedAttempts();
        adminAccountRepository.save(account);

        String accessToken = jwtTokenProvider.createAccessToken(Map.of(
                "storeId", account.getStoreId(),
                "adminId", account.getId(),
                "username", account.getUsername(),
                "role", "ADMIN"
        ));

        String refreshTokenStr = jwtTokenProvider.createRefreshToken();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenStr)
                .adminId(account.getId())
                .storeId(account.getStoreId())
                .role(TokenRole.ADMIN)
                .expiresAt(LocalDateTime.now().plusSeconds(
                        jwtTokenProvider.getRefreshTokenExpiryMs() / 1000))
                .build();
        refreshTokenRepository.save(refreshToken);

        return new TokenResponse(accessToken, refreshTokenStr,
                jwtTokenProvider.getRefreshTokenExpiryMs() / 1000);
    }

    @Transactional
    public TokenResponse refresh(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if (refreshToken.isRevoked()) {
            throw new BusinessException(ErrorCode.TOKEN_REVOKED);
        }
        if (!refreshToken.isValid()) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }
        if (refreshToken.getRole() != TokenRole.ADMIN) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        AdminAccount account = adminAccountRepository.findById(refreshToken.getAdminId())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        String newAccessToken = jwtTokenProvider.createAccessToken(Map.of(
                "storeId", account.getStoreId(),
                "adminId", account.getId(),
                "username", account.getUsername(),
                "role", "ADMIN"
        ));

        return new TokenResponse(newAccessToken, refreshTokenStr,
                jwtTokenProvider.getRefreshTokenExpiryMs() / 1000);
    }

    @Transactional
    public void logout(String refreshTokenStr) {
        refreshTokenRepository.findByToken(refreshTokenStr)
                .ifPresent(RefreshToken::revoke);
    }
}
