package com.project.jejubeach.controller;

import com.project.jejubeach.entity.Beach;
import com.project.jejubeach.entity.User;
import com.project.jejubeach.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "사용자 정보", description = "현재 로그인한 사용자의 정보 및 권한 API")
public class UserController {
    
    private final UserService userService;

    @GetMapping("/role")
    @Operation(summary = "현재 사용자 권한 조회", description = "현재 로그인한 사용자의 역할을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    public ResponseEntity<Map<String, String>> getCurrentUserRole() {
        String username = getCurrentUsername();
        String role = userService.getUserRole(username);
        return ResponseEntity.ok(Map.of("role", role));
    }

    @GetMapping("/beaches")
    @Operation(summary = "현재 사용자가 접근 가능한 해변 조회", description = "현재 로그인한 사용자가 관리할 수 있는 해변들을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    public ResponseEntity<List<Beach>> getAccessibleBeaches() {
        String username = getCurrentUsername();
        List<Beach> beaches = userService.getAccessibleBeaches(username);
        return ResponseEntity.ok(beaches);
    }

    @GetMapping("/profile")
    @Operation(summary = "현재 사용자 프로필 조회", description = "현재 로그인한 사용자의 프로필 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    public ResponseEntity<User> getCurrentUserProfile() {
        String username = getCurrentUsername();
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
