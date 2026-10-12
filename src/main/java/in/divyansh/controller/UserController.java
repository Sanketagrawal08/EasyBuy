package in.divyansh.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.divyansh.dto.request.UpdateUserRequestDTO;
import in.divyansh.dto.request.UserRequestDTO;
import in.divyansh.dto.response.ApiResponseDTO;
import in.divyansh.dto.response.UserResponseDTO;
import in.divyansh.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;


    // =========================
    // CREATE USER
    // =========================

    @PostMapping
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> createUser(
            @Valid @RequestBody UserRequestDTO dto) {

        UserResponseDTO response = userService.createUser(dto);

        ApiResponseDTO<UserResponseDTO> apiResponse =
                ApiResponseDTO.success(
                        "User created successfully",
                        response
                );

        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }


    // =========================
    // GET USER BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getUserById(
            @PathVariable Long id) {

        UserResponseDTO response = userService.getUserById(id);

        ApiResponseDTO<UserResponseDTO> apiResponse =
                ApiResponseDTO.success(
                        "User retrieved successfully",
                        response
                );

        return ResponseEntity.ok(apiResponse);
    }


    // =========================
    // GET USER BY EMAIL
    // =========================

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getUserByEmail(
            @PathVariable String email) {

        UserResponseDTO response =
                userService.getUserByEmail(email);

        ApiResponseDTO<UserResponseDTO> apiResponse =
                ApiResponseDTO.success(
                        "User retrieved successfully",
                        response
                );

        return ResponseEntity.ok(apiResponse);
    }


    // =========================
    // GET ALL USERS
    // =========================

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getAllUsers() {

        List<UserResponseDTO> users =
                userService.getAllUsers();

        ApiResponseDTO<List<UserResponseDTO>> apiResponse =
                ApiResponseDTO.success(
                        "Users retrieved successfully",
                        users
                );

        return ResponseEntity.ok(apiResponse);
    }


    // =========================
    // GET ACTIVE USERS
    // =========================

    @GetMapping("/active")
    public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getActiveUsers() {

        List<UserResponseDTO> users =
                userService.getActiveUsers();

        ApiResponseDTO<List<UserResponseDTO>> apiResponse =
                ApiResponseDTO.success(
                        "Active users retrieved successfully",
                        users
                );

        return ResponseEntity.ok(apiResponse);
    }


    // =========================
    // UPDATE USER
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequestDTO dto) {

        UserResponseDTO response =
                userService.updateUser(id, dto);

        ApiResponseDTO<UserResponseDTO> apiResponse =
                ApiResponseDTO.success(
                        "User updated successfully",
                        response
                );

        return ResponseEntity.ok(apiResponse);
    }


    // =========================
    // ACTIVATE USER
    // =========================

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponseDTO<Void>> activateUser(
            @PathVariable Long id) {

        userService.activateUser(id);

        ApiResponseDTO<Void> apiResponse =
                ApiResponseDTO.success(
                        "User activated successfully"
                );

        return ResponseEntity.ok(apiResponse);
    }


    // =========================
    // DEACTIVATE USER
    // =========================

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponseDTO<Void>> deactivateUser(
            @PathVariable Long id) {

        userService.deactivateUser(id);

        ApiResponseDTO<Void> apiResponse =
                ApiResponseDTO.success(
                        "User deactivated successfully"
                );

        return ResponseEntity.ok(apiResponse);
    }


    // =========================
    // SEARCH USERS
    // =========================

    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> searchUsers(
            @RequestParam String keyword) {

        List<UserResponseDTO> users =
                userService.searchUser(keyword);

        ApiResponseDTO<List<UserResponseDTO>> apiResponse =
                ApiResponseDTO.success(
                        "Users found successfully",
                        users
                );

        return ResponseEntity.ok(apiResponse);
    }


    // =========================
    // CHECK EMAIL
    // =========================

    @GetMapping("/check-email")
    public ResponseEntity<ApiResponseDTO<Boolean>> checkEmail(
            @RequestParam String email) {

        boolean exists =
                userService.existsByEmail(email);

        ApiResponseDTO<Boolean> apiResponse =
                ApiResponseDTO.success(
                        "Email availability checked",
                        exists
                );

        return ResponseEntity.ok(apiResponse);
    }
}