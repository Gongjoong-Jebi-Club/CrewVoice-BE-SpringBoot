package org.skala.crewvoice.service;

import org.skala.crewvoice.domain.Role;
import org.skala.crewvoice.domain.User;
import org.skala.crewvoice.domain.UserStatus;
import org.skala.crewvoice.dto.request.ResetPasswordRequest;
import org.skala.crewvoice.dto.response.DeleteUserResponse;
import org.skala.crewvoice.dto.response.UserResponse;
import org.skala.crewvoice.exception.ErrorCode;
import org.skala.crewvoice.exception.custom.EntityNotFoundException;
import org.skala.crewvoice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAllActiveUsers() {
        return userRepository.findByStatus(UserStatus.ACTIVE)
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    public List<UserResponse> searchUsers(String keyword, String role) {
        if ((keyword == null || keyword.isBlank()) && (role == null || role.isBlank())) {
            return getAllActiveUsers();
        }

        // String role을 Enum으로 변환
        Role roleEnum = null;
        if (role != null && !role.isBlank()) {
            String roleUpper = role.toUpperCase();
            switch (roleUpper) {
                case "ADMIN":
                    roleEnum = Role.ADMIN;
                    break;
                case "MANAGER":
                    roleEnum = Role.MANAGER;
                    break;
                case "CREW":
                    roleEnum = Role.CREW;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid role: " + role + ". Must be ADMIN, MANAGER, or CREW");
            }
        }

        return userRepository.searchUsers(keyword, roleEnum, UserStatus.ACTIVE)
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.resetPassword(encodedPassword);
    }

    @Transactional
    public DeleteUserResponse deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        user.delete();
        return new DeleteUserResponse(user.getId());
    }
}
