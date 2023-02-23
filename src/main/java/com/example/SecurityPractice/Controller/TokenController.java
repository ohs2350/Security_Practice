package com.example.SecurityPractice.Controller;

import com.example.SecurityPractice.DTO.UserJwtDTO;
import com.example.SecurityPractice.Service.UserService;
import com.example.SecurityPractice.security.jwt.HeaderTokenExtractor;
import com.example.SecurityPractice.security.jwt.JwtDecoder;
import com.example.SecurityPractice.security.jwt.JwtFactory;
import com.example.SecurityPractice.security.jwt.RedisService;
import com.example.SecurityPractice.security.token.PostAuthorizationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class TokenController {

    @Autowired
    JwtFactory jwtFactory;
    @Autowired
    JwtDecoder jwtDecoder;
    @Autowired
    HeaderTokenExtractor extractor;
    @Autowired
    RedisService redisService;
    @Autowired
    UserService userService;

    // 재발급 요청 - access token 이 expire 되면 401응답 -> 클라이언트에서 재발급 요청
    @PostMapping("refresh")
    public String refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String tokenPayload = request.getHeader("Authorization");
        String accessToken = extractor.extract(tokenPayload, request);

        // decode 과정에서 유효성 확인
        UserJwtDTO userJwtDTO = jwtDecoder.decodeJwt(accessToken);
        String refreshToken = redisService.getRefreshToken(userJwtDTO.getId());

        // UserDetails 객체 생성
        UserDetails userDetails = userService.loadUserByUsername(userJwtDTO.getId());

        // refresh token 이 유효한지 검사 후 access token 재발행
        if (jwtDecoder.canTokenBeRefreshed(refreshToken)) {
            String token = jwtFactory.generateAccessToken(userDetails);
            System.out.println("access token 재발급 : " + token);

            // HttpOnly 옵션이 설정된 쿠키에 access token 저장
            Cookie cookie = new Cookie("ACCESS_TOKEN", token);
            cookie.setHttpOnly(true); // HttpOnly 옵션 설정
//        cookie.setSecure(true); // Secure 옵션 설정
            response.addCookie(cookie);

            return "index";
        }
        else {
            System.out.println("refresh token 유효성 검증 실패");
            return "login";
        }
    }
}
