package com.example.SecurityPractice.security.filter;

import com.example.SecurityPractice.DTO.UserDTO;
import com.example.SecurityPractice.security.token.PreAuthorizationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FormLoginFilter extends AbstractAuthenticationProcessingFilter {
    /**
     * 3.
     * 인증 성공 or 실패 메서드 구현하기 위해서 필요한 성공실패 인터페이스를 불러옴.
     * */
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    private AuthenticationFailureHandler authenticationFailureHandler;

    /**
     * 1.
     * defaultFilterProcessesUrl - filterProcessesUrl 의 기본생성자 생성
     * 그리고 성공 실패 핸들러를 담은 생성자 두개 생성
     * */
    protected FormLoginFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    public FormLoginFilter(
            AntPathRequestMatcher defaultUrl,
            AuthenticationSuccessHandler successHandler,
            AuthenticationFailureHandler failureHandler) {
        super(defaultUrl);
        this.authenticationSuccessHandler = successHandler;
        this.authenticationFailureHandler = failureHandler;
    }

    /**
     * 2.
     * attemptAuthentication 메서드를 @Override
     * AbstractAuthenticationProcessingFilter 클래스의 doFilter 메서드로 인해서
     * 가장 처음 인증 attemptAuthentication 메서드를 실행
     *
     * 만약 인증이 성공한다면 doFilter 메서드 에서 successfulAuthentication 으로 메서드를 실행시키도록 해준다. (인증 실패도 동일)
     * */
    @Override
    public Authentication attemptAuthentication( HttpServletRequest req, HttpServletResponse res )
            throws AuthenticationException, IOException, ServletException {
        // JSON 으로 변환 - 사용자입력을 req 로 받고 값을 ObjectMapper 객체로 JSON 으로 변환하여 DTO 형식으로 저장
        UserDTO dto = new ObjectMapper().readValue(
                req.getReader(),
                UserDTO.class
        );

        // 사용자입력값이 존재하는지 비교하기 위해서 DTO 를 인증 '전' Token 객체에 넣어 PreAuthorizationToken 을 생성
        PreAuthorizationToken token = new PreAuthorizationToken(dto);

        // 위 사용자의 값을 가지고 attemptAuthentication 는 인증을 시도 (인증 시도는 FormLoginAuthenticationProvider 에서)

        // PreAuthorizationToken 해당 객체에 맞는 Provider를
        // getAuthenticationManager 해당 메서드가 자동으로 찾아서 연결해 준다.
        // 자동으로 찾아준다고 해도 Provider 에 직접 PreAuthorizationToken 지정해 줘야 찾아갑니다.

        return super
                .getAuthenticationManager()
                .authenticate(token);
    }

    /**
     * 4.
     * 인증 성공 or 실패 메서드 구현
     * */
    @Override
    protected void successfulAuthentication(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain,
            Authentication authResult
    ) throws IOException, ServletException {
        this
                .authenticationSuccessHandler
                .onAuthenticationSuccess(req, res, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest req,
            HttpServletResponse res,
            AuthenticationException failed
    ) throws IOException, ServletException {
        this
                .authenticationFailureHandler
                .onAuthenticationFailure(req, res, failed);
    }
}
