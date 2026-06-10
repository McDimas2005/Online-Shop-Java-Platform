package com.mcdimas.onlineshop.security;

import com.mcdimas.onlineshop.repository.UserAccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ShopUserDetailsService implements UserDetailsService {
    private final UserAccountRepository userAccountRepository;

    public ShopUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userAccountRepository.findByEmailIgnoreCase(username)
                .map(account -> new User(account.getEmail(), account.getPasswordHash(),
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()))))
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }
}
