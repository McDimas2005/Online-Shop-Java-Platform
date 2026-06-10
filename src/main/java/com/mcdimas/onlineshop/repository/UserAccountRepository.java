package com.mcdimas.onlineshop.repository;

import com.mcdimas.onlineshop.entity.UserAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmailIgnoreCase(String email);

    Optional<UserAccount> findByUserCode(String userCode);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUserCode(String userCode);
}
