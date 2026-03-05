package com.tableorder.admin.domain;

import com.tableorder.admin.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_accounts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"store_id", "username"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "is_locked", nullable = false)
    private boolean locked = false;

    @Column(name = "lock_until")
    private LocalDateTime lockUntil;

    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts = 0;

    public static AdminAccount create(Long storeId, String username, String passwordHash) {
        AdminAccount account = new AdminAccount();
        account.storeId = storeId;
        account.username = username;
        account.passwordHash = passwordHash;
        return account;
    }

    public void recordFailedAttempt(int maxAttempts, int lockMinutes) {
        this.failedAttempts++;
        if (this.failedAttempts >= maxAttempts) {
            this.locked = true;
            this.lockUntil = LocalDateTime.now().plusMinutes(lockMinutes);
        }
    }

    public void resetFailedAttempts() {
        this.failedAttempts = 0;
        this.locked = false;
        this.lockUntil = null;
    }

    public boolean isCurrentlyLocked() {
        if (!locked) return false;
        if (lockUntil != null && lockUntil.isBefore(LocalDateTime.now())) {
            this.locked = false;
            return false;
        }
        return true;
    }
}
