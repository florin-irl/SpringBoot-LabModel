package com.example.rest_api.users.database.service;

import com.example.rest_api.users.database.model.RoleEntity;
import com.example.rest_api.users.database.model.UserEntity;
import com.example.rest_api.users.database.repository.RoleRepository;
import com.example.rest_api.users.database.repository.UserRepository;
import com.example.rest_api.users.database.security.AuthenticatedUser;
import com.example.rest_api.users.database.security.PasswordGeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService extends OidcUserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /* Authentication methods */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity entity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return entity.toAuthenticatedUser();
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();

        // Check if user exists in the database
        Optional<UserEntity> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) {
            // Create a new user if it doesn't exist
            UserEntity newUser = new UserEntity();
            newUser.setEmail(email);
            newUser.setUsername(name != null ? name : email);
            newUser.setPassword(PasswordGeneratorUtil.generate()); // Generate a random password
            newUser.setIsOAuthAccount(true);

            // Assign a default role
            RoleEntity defaultRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new IllegalArgumentException("Default role not found"));
            newUser.setRoles(List.of(defaultRole));

            userRepository.save(newUser);
        }

        // Return an authenticated user
        return optUser.orElseGet(() -> userRepository.findByEmail(email).get()).toAuthenticatedUser();
    }


    /* CRUD Operations */

    public UserEntity save(UserEntity user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public List<UserEntity> findAll() {
        return this.userRepository.findAll();
    }

    public Optional<UserEntity> findById(Long id) {
        return this.userRepository.findById(id);
    }

    public void updateUserRoles(Long userId, List<Long> roleIds) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<RoleEntity> roles = roleRepository.findAllById(roleIds);
        user.setRoles(roles);
        userRepository.save(user);
    }



}
