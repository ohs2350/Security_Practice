package com.example.SecurityPractice.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtFactory {

    /**
     * 1.
     * JWT Token 을 만들어주는 메서드, JWT 값으로 유저 아이디, 유저 권한, 토큰 유효시간 을 포함
     * */
    public String generateToken(UserDetails userDetails) {
        String token = null;
        try {
            Set<String> roles = userDetails.getAuthorities().stream()
                    .map(r -> r.getAuthority()).collect(Collectors.toSet());
            String role = roles.iterator().next();

            token = JWT.create()
                    .withIssuer("jjunpro")
                    .withClaim("USERNAME", userDetails.getUsername())
                    .withClaim("USER_ROLE", role)
                    .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)))
                    .sign(generateAlgorithm());

        } catch (Exception e) {
            // 추후 로그로 변경
            System.out.println(e.getMessage());
        }

        return token;
    }

    // 2. signature 서명 값을 선언해주고 Algorithm generateAlgorithm() 암호화 메서드로 암호화 후 값을 넣어줍니다.
    private Algorithm generateAlgorithm() throws UnsupportedEncodingException {
        String signingKey = "jwttest";
        return Algorithm.HMAC256(signingKey);
    }
}
