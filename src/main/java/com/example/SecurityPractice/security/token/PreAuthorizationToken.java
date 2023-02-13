package com.example.SecurityPractice.security.token;

import com.example.SecurityPractice.DTO.UserDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class PreAuthorizationToken extends UsernamePasswordAuthenticationToken {
    // 인증 전 토큰 - UsernamePasswordAuthenticationToken(사용자 이름 비밀번호 인증 토큰 클래스) 상속

    private PreAuthorizationToken(String name, String password) {
        super(name, password);
    }

    public PreAuthorizationToken(UserDTO dto) {
        this(dto.getName(), dto.getPassword());
    }

    public String getUsername() {
        return (String) super.getPrincipal();
    }

    public String getUserPassword() {
        return (String) super.getCredentials();
    }
}
