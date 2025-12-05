package com.vinisnzy.connectus_api.infra.security.service;

import com.vinisnzy.connectus_api.domain.core.entity.User;
import com.vinisnzy.connectus_api.domain.core.repository.UserRepository;
import com.vinisnzy.connectus_api.infra.security.entity.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));

        return new UserDetailsImpl(
                user.getId(),
                user.getCompany().getId(),
                user.getRole().getName(),
                user.getName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getIsActive(),
                user.getIsMaster(),
                user.getRole().getPermissions()
        );
    }
}
