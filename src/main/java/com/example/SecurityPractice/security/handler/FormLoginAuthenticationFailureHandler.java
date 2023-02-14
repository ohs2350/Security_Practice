package com.example.SecurityPractice.security.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class FormLoginAuthenticationFailureHandler implements AuthenticationFailureHandler {

    // 로그인 실패 시 호출
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) {
        // 추후 로그로 변경
        System.out.println("로그인 실패 헨들러 실행, " + exception.getMessage());
    }
}
