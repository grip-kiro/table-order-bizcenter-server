package com.tableorder.admin.repository;

import com.tableorder.admin.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
