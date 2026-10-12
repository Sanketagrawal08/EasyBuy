package in.divyansh.service;

import java.util.List;

import in.divyansh.dto.request.UpdateUserRequestDTO;
import in.divyansh.dto.request.UserRequestDTO;
import in.divyansh.dto.response.UserResponseDTO;

public interface UserService {

    UserResponseDTO createUser(UserRequestDTO userRequest);

    UserResponseDTO getUserById(Long id);

    UserResponseDTO getUserByEmail(String email);

    List<UserResponseDTO> getAllUsers();

    List<UserResponseDTO> getActiveUsers();

    UserResponseDTO updateUser(Long id, UpdateUserRequestDTO userRequest);

    void activateUser(Long id);

    void deactivateUser(Long id);

    List<UserResponseDTO> searchUser(String keyword);

    boolean existsByEmail(String email);
}