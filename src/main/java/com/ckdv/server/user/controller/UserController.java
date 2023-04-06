package com.ckdv.server.user.controller;

import com.ckdv.server.UserSecurity.dao.JpaUserDetailsService;
import com.ckdv.server.user.request.LoginRequest;
import com.ckdv.server.config.JwtUtils;
import com.ckdv.server.user.request.RegisterRequest;
import com.ckdv.server.user.model.User;
import com.ckdv.server.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final JpaUserDetailsService jpaUserDetailsService;
    private final UserService userService;

    /** 사용자 로그인, JWT 토큰 발급 */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword(), new ArrayList<>()));

            final UserDetails user = jpaUserDetailsService.loadUserByUsername(request.getEmail());

            if (user != null) {
                String jwt = jwtUtils.generateToken(user);

                Cookie cookie = new Cookie("jwt", jwt);

                cookie.setMaxAge(7 * 24 * 60 * 60);                             // expires in 7 days
                cookie.setHttpOnly(true);
                cookie.setPath("/");                                            // Global
                response.addCookie(cookie);

                return ResponseEntity.ok(jwt);
            }

            return ResponseEntity.status(400).body("인증에 실패하였습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("" + e.getMessage());
        }
    }

    /** 신규 사용자 등록 */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest user) {
        User newUser = userService.addUser(user);

        if(newUser != null) {
            return ResponseEntity.ok().body("등록 성공");
        } else {
            return ResponseEntity.status(400).body("신규 사용자 등록 중 오류가 발생하였습니다.");
        }
    }

    /** 사용자 목록 조회 (ADMIN) */
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping("/getAllUsers")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
