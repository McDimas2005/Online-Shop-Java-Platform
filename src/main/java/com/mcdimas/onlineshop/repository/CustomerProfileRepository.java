package com.mcdimas.onlineshop.repository;

import com.mcdimas.onlineshop.entity.CustomerProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {
    Optional<CustomerProfile> findByUserEmailIgnoreCase(String email);
}
