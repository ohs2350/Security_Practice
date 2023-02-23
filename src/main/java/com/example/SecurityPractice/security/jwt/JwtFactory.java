package com.example.SecurityPractice.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtFactory {
    @Value("${jwt.access.secret}")
    private String accessSecret;

    @Value("${jwt.refresh.secret}")
    private String refreshSecret;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

//    @Value("${jwt.expiration}")
//    private int jwtExpiration;


    /**
     * JWT Access Token 생성 메서드, JWT 값으로 유저 아이디, 유저 권한, 토큰 유효시간 을 포함
     * */
    public String generateAccessToken(UserDetails userDetails) {
        String token = null;
        try {
            Set<String> roles = userDetails.getAuthorities().stream()
                    .map(r -> r.getAuthority()).collect(Collectors.toSet());
            String role = roles.iterator().next();

            token = JWT.create()
                    .withIssuer(jwtIssuer)
                    .withClaim("USERNAME", userDetails.getUsername())
                    .withClaim("USER_ROLE", role)
                    .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 2))) // 2시간
                    .sign(generateAlgorithm(accessSecret));

        } catch (Exception e) {
            // 추후 로그로 변경
            System.out.println(e.getMessage());
        }

        return token;
    }

    /**
     * JWT Refresh Token 생성 메서드, JWT 값으로 페이로드엔 아무 값도 넣지 않음
     * */
    public String generateRefreshToken() {
        String token = null;
        try {
            // 유효기간 2주, 페이로드에 값 담지 않음
            token = JWT.create()
                    .withIssuer(jwtIssuer)
                    .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 14)))
                    .sign(generateAlgorithm(refreshSecret));

        } catch (Exception e) {
            // 추후 로그로 변경
            System.out.println(e.getMessage());
        }

        return token;
    }

    // 2. signature 서명 값을 선언해주고 Algorithm generateAlgorithm() 암호화 메서드로 암호화 후 값을 넣어줍니다.
    private Algorithm generateAlgorithm(String key) throws UnsupportedEncodingException {
        return Algorithm.HMAC256(key);
    }

}
