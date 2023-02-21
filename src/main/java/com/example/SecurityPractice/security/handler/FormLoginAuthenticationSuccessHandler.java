package com.example.SecurityPractice.security.handler;

import com.example.SecurityPractice.DTO.TokenDTO;
import com.example.SecurityPractice.security.jwt.JwtFactory;
import com.example.SecurityPractice.security.jwt.RedisService;
import com.example.SecurityPractice.security.token.PostAuthorizationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FormLoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    // AuthenticationSuccessHandler 구현체에서는 로그인을 성공했을때 호출(인증 객체가 생성되어진 후)

    // 2.
    private final JwtFactory factory;
    private final ObjectMapper objectMapper;
    private  final RedisService redisService;

    /**
     * 1.
     * Token 값을 정형화된 DTO를 만들어서 res 으로 내려주는 역할을 수행
     * 인증결과 객체 auth 를 PostAuthorizationToken 객체 변수에 담아준다.
     * */
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest req,
            HttpServletResponse res,
            Authentication auth
    ) throws IOException {
        System.out.println("로그인 성공 헨들러");

        PostAuthorizationToken token   = (PostAuthorizationToken) auth;
        UserDetails userDetails = token.getUserDetails();

        /**
         * 2.
         * PostAuthorizationToken 객체에 담아줄 JWT Token 을 생성해야 한다.
         * 그 후 processRespone 메서드를 통해서 Response 상태와 jwt 값을 전송
         * */
        String tokenString = factory.generateAccessToken(userDetails);
        String refresh = factory.generateRefreshToken();

        // HttpOnly 옵션이 설정된 쿠키에 access token 저장
        Cookie cookie = new Cookie("ACCESS_TOKEN", tokenString);
        cookie.setHttpOnly(true); // HttpOnly 옵션 설정
//        cookie.setSecure(true); // Secure 옵션 설정
        res.addCookie(cookie);

        // 3. Access Token 값을 정형화된 DTO 를 생성
        TokenDTO tokenDTO = new TokenDTO(tokenString, userDetails.getUsername());

        // 4. Refresh Token 값 Redis에 저장 (토큰 : 유저아이디 형식)
        redisService.insertRefreshToken(userDetails.getUsername(), refresh);

        processResponse(res, tokenDTO);
    }

    private void processResponse(
            HttpServletResponse res,
            TokenDTO dto
    ) throws IOException {
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setStatus(HttpStatus.OK.value());
        res.getWriter().write(objectMapper.writeValueAsString(dto));
    }

}
