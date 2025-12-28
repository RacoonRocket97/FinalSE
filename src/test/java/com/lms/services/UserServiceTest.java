package com.lms.services;

import com.lms.dto.request.PasswordChangeDto;
import com.lms.dto.request.UserRequestDto;
import com.lms.dto.response.UserResponseDto;
import com.lms.mappers.UserMapper;
import com.lms.models.Role;
import com.lms.models.User;
import com.lms.repositories.RoleRepository;
import com.lms.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testLoadUserByUsername_Success() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername(email);

        assertNotNull(result);
        assertEquals(email, result.getUsername());
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        String email = "missing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(email);
        });
    }

    @Test
    void testRegisterUser_Success() {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setEmail("new@student.com");
        requestDto.setPassword("password123");

        Role role = new Role();
        role.setRoleName("ROLE_STUDENT");

        User userEntity = new User();
        User savedUser = new User();
        savedUser.setId(1L);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(1L);

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(requestDto)).thenReturn(userEntity);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPass");
        when(roleRepository.findByRoleName("ROLE_STUDENT")).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponseDto(savedUser)).thenReturn(responseDto);

        UserResponseDto result = userService.registerUser(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailExists() {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setEmail("existing@student.com");

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(requestDto);
        });

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void testUpdateProfile_Success() {
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("me@lms.com");

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(currentUser);
        when(authentication.isAuthenticated()).thenReturn(true); // User is logged in
        SecurityContextHolder.setContext(securityContext);

        UserRequestDto updateDto = new UserRequestDto();
        updateDto.setFirstName("UpdatedName");

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setFirstName("UpdatedName");

        when(userRepository.save(any(User.class))).thenReturn(currentUser);
        when(userMapper.toResponseDto(currentUser)).thenReturn(responseDto);

        UserResponseDto result = userService.updateProfile(updateDto);

        assertEquals("UpdatedName", result.getFirstName());
    }

    @Test
    void testChangePassword_Success() {
        User currentUser = new User();
        currentUser.setPassword("encodedOldPassword");

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(currentUser);
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.setContext(securityContext);

        PasswordChangeDto changeDto = new PasswordChangeDto();
        changeDto.setOldPassword("plainOldPassword");
        changeDto.setNewPassword("newPassword");
        changeDto.setConfirmPassword("newPassword");

        when(passwordEncoder.matches("plainOldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        boolean result = userService.changePassword(changeDto);

        assertTrue(result);
        verify(userRepository).save(currentUser);
    }

    @Test
    void testBlockUser_Success() {
        Long userId = 1L;
        User user = new User();
        user.setIsActive(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        boolean result = userService.blockUser(userId);

        assertTrue(result);
        assertFalse(user.getIsActive()); // Verify the flag changed to false
        verify(userRepository).save(user);
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(userMapper.toResponseDtoList(any())).thenReturn(Collections.emptyList());

        List<UserResponseDto> result = userService.getAllUsers();
        assertNotNull(result);
    }
}