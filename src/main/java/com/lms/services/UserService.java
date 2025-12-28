package com.lms.services;

import com.lms.dto.request.PasswordChangeDto;
import com.lms.dto.request.UserRequestDto;
import com.lms.dto.response.UserResponseDto;
import com.lms.mappers.UserMapper;
import com.lms.models.Role;
import com.lms.models.User;
import com.lms.repositories.RoleRepository;
import com.lms.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // This method is from UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    public UserResponseDto registerUser(UserRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        Role studentRole = roleRepository.findByRoleName("ROLE_STUDENT")
                .orElseThrow(() -> new RuntimeException("ROLE_STUDENT not found"));

        user.setRoles(List.of(studentRole));

        User savedUser = userRepository.save(user);
        return userMapper.toResponseDto(savedUser);
    }

    public UserResponseDto createUser(UserRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            List<Role> roles = new ArrayList<>();
            for (Long roleId : dto.getRoleIds()) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() ->
                                new RuntimeException("Role not found with id: " + roleId));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);
        return userMapper.toResponseDto(savedUser);
    }

    public List<UserResponseDto> getAllUsers() {
        return userMapper.toResponseDtoList(userRepository.findAll());
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + id));
        return userMapper.toResponseDto(user);
    }

    public UserResponseDto updateProfile(UserRequestDto dto) {
        User currentUser = getCurrentUser();

        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        currentUser.setFirstName(dto.getFirstName());
        currentUser.setLastName(dto.getLastName());
        currentUser.setBirthDate(dto.getBirthDate());
        currentUser.setPhoneNumber(dto.getPhoneNumber());

        return userMapper.toResponseDto(userRepository.save(currentUser));
    }

    public boolean changePassword(PasswordChangeDto dto) {
        User currentUser = getCurrentUser();

        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), currentUser.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("New passwords do not match");
        }
        currentUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(currentUser);
        return true;
    }

    public boolean blockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + id));

        user.setIsActive(false);
        userRepository.save(user);
        return true;
    }

    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        return true;
    }
}