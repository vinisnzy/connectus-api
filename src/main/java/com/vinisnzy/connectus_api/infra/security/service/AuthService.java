package com.vinisnzy.connectus_api.infra.security.service;

import com.vinisnzy.connectus_api.domain.core.entity.Company;
import com.vinisnzy.connectus_api.domain.core.entity.Role;
import com.vinisnzy.connectus_api.domain.core.entity.User;
import com.vinisnzy.connectus_api.domain.core.repository.CompanyRepository;
import com.vinisnzy.connectus_api.domain.core.repository.RoleRepository;
import com.vinisnzy.connectus_api.domain.core.repository.UserRepository;
import com.vinisnzy.connectus_api.domain.core.service.CompanyService;
import com.vinisnzy.connectus_api.domain.core.service.RoleService;
import com.vinisnzy.connectus_api.infra.security.dto.request.CreateUserRequest;
import com.vinisnzy.connectus_api.infra.security.dto.request.LoginRequest;
import com.vinisnzy.connectus_api.infra.security.dto.response.LoginResponse;
import com.vinisnzy.connectus_api.infra.security.dto.request.RegisterUserRequest;
import com.vinisnzy.connectus_api.infra.security.dto.response.RegisterUserResponse;
import com.vinisnzy.connectus_api.infra.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final CompanyService companyService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public LoginResponse login(LoginRequest request) {
        Authentication usernamePassword = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        var auth = authenticationManager.authenticate(usernamePassword);
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + request.email()));
        return new LoginResponse(tokenService.generateToken(user));
    }

    public RegisterUserResponse register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Esse email já está sendo utilizado");
        }

        Company company = companyService.create(request.company());
        Role role = roleService.findRoleByName("MASTER");

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User();
        user.setCompany(company);
        user.setRole(role);
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordHash);
        user.setPhone(request.phone());
        user.setIsMaster(true);
        user = userRepository.save(user);

        return new RegisterUserResponse(
                user.getId(),
                request.email(),
                request.name()
        );
    }

    public RegisterUserResponse createUser(CreateUserRequest request) {
        UUID companyId = SecurityUtils.getCurrentCompanyIdOrThrow();
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Esse email já está sendo utilizado");
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalStateException("Não existe uma empresa com o id: " + companyId));

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new IllegalStateException("Não existe uma função com o id: " + request.roleId()));

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User();
        user.setCompany(company);
        user.setRole(role);
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordHash);
        user.setPhone(request.phone());
        user.setIsMaster(request.isMaster());
        user = userRepository.save(user);

        return new RegisterUserResponse(
                user.getId(),
                request.email(),
                request.name()
        );
    }
}
