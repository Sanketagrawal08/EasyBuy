package in.divyansh.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.divyansh.dto.request.UpdateUserRequestDTO;
import in.divyansh.dto.request.UserRequestDTO;
import in.divyansh.dto.response.UserResponseDTO;
import in.divyansh.entity.Cart;
import in.divyansh.entity.User;
import in.divyansh.exception.DuplicateResourceException;
import in.divyansh.exception.InvalidOperationException;
import in.divyansh.exception.ResourceNotFoundException;
import in.divyansh.repository.UserRepository;
import in.divyansh.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {

        // Check if email already exists
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new DuplicateResourceException(
                    "User",
                    "email",
                    userRequestDTO.getEmail()
            );
        }

        // Create user entity
        User user = User.builder()
                .fullName(userRequestDTO.getFullName())
                .email(userRequestDTO.getEmail())
                .password(userRequestDTO.getPassword()) // In real app, encode password
                .phone(userRequestDTO.getPhone())
                .address(userRequestDTO.getAddress())
                .build();

        // Create cart for user
        Cart cart = Cart.builder()
                .user(user)
                .build();

        user.setCart(cart);

        User savedUser = userRepository.save(user);

        return mapToResponseDTO(savedUser);
    }

    @Override
    public UserResponseDTO getUserById(Long id) {

        User user = findUserById(id);

        return mapToResponseDTO(user);
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            return mapToResponseDTO(userOptional.get());
        } else {
            throw new ResourceNotFoundException(
                    "User",
                    "email",
                    email
            );
        }
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {

        List<User> users = userRepository.findAll();

        List<UserResponseDTO> responseList = new ArrayList<>();

        for (User user : users) {
            responseList.add(mapToResponseDTO(user));
        }

        return responseList;
    }

    @Override
    public List<UserResponseDTO> getActiveUsers() {

        List<User> activeUsers = userRepository.findByIsActiveTrue();

        List<UserResponseDTO> responseList = new ArrayList<>();

        for (User user : activeUsers) {
            responseList.add(mapToResponseDTO(user));
        }

        return responseList;
    }

    @Override
    public UserResponseDTO updateUser(Long id, UpdateUserRequestDTO dto) {

        User user = findUserById(id);

        // At least one field must be provided
        if (dto.getFullName() == null &&
            dto.getPassword() == null &&
            dto.getPhone() == null &&
            dto.getEmail() == null &&
            dto.getAddress() == null) {

            throw new InvalidOperationException(
                    "At least one field must be provided for update"
            );
        }

        // Update full name
        if (dto.getFullName() != null) {

            if (dto.getFullName().isBlank()) {
                throw new IllegalArgumentException(
                        "Full name cannot be blank"
                );
            }

            user.setFullName(dto.getFullName().trim());
        }

        // Update password
        if (dto.getPassword() != null) {

            if (dto.getPassword().isBlank()) {
                throw new IllegalArgumentException(
                        "Password cannot be blank"
                );
            }

            user.setPassword(dto.getPassword()); // Later you can encode
        }

        // Update phone
        if (dto.getPhone() != null) {

            if (dto.getPhone().isBlank()) {
                throw new IllegalArgumentException(
                        "Phone cannot be blank"
                );
            }

            user.setPhone(dto.getPhone().trim());
        }

        // Update email
        if (dto.getEmail() != null) {

            if (dto.getEmail().isBlank()) {
                throw new IllegalArgumentException(
                        "Email cannot be blank"
                );
            }

//           checking Existing Email
            boolean res =userRepository.existsByEmail(dto.getEmail());
            if(res==true ) {
            	throw new DuplicateResourceException(" Email Already in Use ");
            }
            user.setEmail(dto.getEmail().trim());
            		
        }

        // Update address
        if (dto.getAddress() != null) {

            if (dto.getAddress().isBlank()) {
                throw new IllegalArgumentException(
                        "Address cannot be blank"
                );
            }

            user.setAddress(dto.getAddress().trim());
        }

        User updatedUser = userRepository.save(user);

        return mapToResponseDTO(updatedUser);
    }

    @Override
    public void activateUser(Long id) {

        User user = findUserById(id);

        user.setIsActive(true);

        userRepository.save(user);
    }

    @Override
    public void deactivateUser(Long id) {

        User user = findUserById(id);

        user.setIsActive(false);

        userRepository.save(user);
    }

    @Override
    public List<UserResponseDTO> searchUser(String keyword) {

        List<User> users = userRepository.searchByNameOrEmail(keyword);

        List<UserResponseDTO> responseList = new ArrayList<>();

        for (User user : users) {
            responseList.add(mapToResponseDTO(user));
        }

        return responseList;
    }

    @Override
    public boolean existsByEmail(String email) {

        return userRepository.existsByEmail(email);
    }

    // Helper method to find user by ID
    private User findUserById(Long id) {

        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new ResourceNotFoundException(
                    "User",
                    "id",
                    id
            );
        }
    }

    // Helper method to map User entity to UserResponseDTO
    private UserResponseDTO mapToResponseDTO(User user) {

        return UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .isActive(user.getIsActive())
                .build();
    }
}