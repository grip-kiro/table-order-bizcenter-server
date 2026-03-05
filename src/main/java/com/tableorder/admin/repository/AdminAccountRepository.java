package com.tableorder.admin.repository;

import com.tableorder.admin.domain.AdminAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminAccountRepository extends JpaRepository<AdminAccount, Long> {
    Optional<AdminAccount> findByStoreIdAndUsername(Long storeId, String username);
}
