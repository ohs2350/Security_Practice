package com.example.SecurityPractice.security.handler;

import com.example.SecurityPractice.DTO.UserJwtDTO;
import com.example.SecurityPractice.security.jwt.HeaderTokenExtractor;
import com.example.SecurityPractice.security.jwt.JwtDecoder;
import com.example.SecurityPractice.security.jwt.RedisService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;

@Component
public class CustomLogoutHandler implements LogoutHandler {

    HeaderTokenExtractor extractor;
    RedisService redisService;
    JwtDecoder jwtDecoder;

    public CustomLogoutHandler(HeaderTokenExtractor extractor, RedisService redisService, JwtDecoder jwtDecoder) {
        this.extractor = extractor;
        this.redisService = redisService;
        this.jwtDecoder = jwtDecoder;
    }


    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        System.out.println("로그아웃 헨들러 실행");

        String tokenPayload = request.getHeader("Authorization");
        String accessToken = extractor.extract(tokenPayload, request);

        if (accessToken != null) {
            UserJwtDTO userJwtDTO = jwtDecoder.decodeJwt(accessToken);
            redisService.deleteRefreshToken(userJwtDTO.getId());
        }
        else {
            throw new NoSuchElementException("Access Token 이 존재하지 않습니다.");
        }



        // 1. 먼저 요청받은 AccessToken 유효성을 검증
        // 2. 액세스 토큰을 통해 Authentication 객체를 그리고 저장된 User 정보를 가져오기
        // 3. user (Redis key 값)을 통해 저장된 RefreshToken 이 있는지 여부를 확인하여 있다면 삭제
        // 4. 리프레시 토큰과 액세스 토큰을 지우기
    }

}
