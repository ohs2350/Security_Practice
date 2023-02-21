package com.example.SecurityPractice.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.SecurityPractice.DTO.UserDTO;
import com.example.SecurityPractice.DTO.UserJwtDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Optional;

@Component
public class JwtDecoder {

    @Value("${jwt.access.secret}")
    private String accessSecret;

    @Value("${jwt.refresh.secret}")
    private String refreshSecret;

    public UserJwtDTO decodeJwt(String token) {
        DecodedJWT decodedJWT = isValidToken(token)
                .orElseThrow(() -> new NoSuchElementException("유효한 토큰이 아닙니다."));

        String username = decodedJWT
                .getClaim("USERNAME")
                .asString();

        String role = decodedJWT
                .getClaim("USER_ROLE")
                .asString();

        return new UserJwtDTO(username, role);
    }

    private Optional<DecodedJWT> isValidToken(String token) {
        DecodedJWT jwt = null;

        try {
            Algorithm algorithm = Algorithm.HMAC256(accessSecret);
            JWTVerifier verifier = JWT
                    .require(algorithm)
                    .build();

            jwt = verifier.verify(token);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return Optional.ofNullable(jwt);
    }

//    // JWT에서 사용자 이름을 추출
//    public String getUsernameFromJwtToken(String token) {
//        // import가 다름
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(jwtSecret.getBytes())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//
//        return claims.getSubject();
//    }
}
