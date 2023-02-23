package com.example.SecurityPractice.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.SecurityPractice.DTO.UserDTO;
import com.example.SecurityPractice.DTO.UserJwtDTO;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.security.SignatureException;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
public class JwtDecoder {

    @Value("${jwt.access.secret}")
    private String accessSecret;

    @Value("${jwt.refresh.secret}")
    private String refreshSecret;

    // 토큰 decode
    public UserJwtDTO decodeJwt(String token) {
        DecodedJWT decodedJWT = isValidAccessToken(token)
                .orElseThrow(() -> new NoSuchElementException("유효한 토큰이 아닙니다."));

        String username = decodedJWT
                .getClaim("USERNAME")
                .asString();

        String role = decodedJWT
                .getClaim("USER_ROLE")
                .asString();

        return new UserJwtDTO(username, role);
    }

    private Optional<DecodedJWT> isValidAccessToken(String token) {
        return isValidToken(token, "access");
    }

    private Optional<DecodedJWT> isValidRefreshToken(String token) {
        return isValidToken(token, "refresh");
    }


    // 토큰 유효성 검증
    private Optional<DecodedJWT> isValidToken(String token, String kind) {
        DecodedJWT jwt = null;
        String key = null;

        if (kind == "access") {key = accessSecret;}
        else if (kind == "refresh") {key = refreshSecret;}

        try {
            Algorithm algorithm = Algorithm.HMAC256(key);
            JWTVerifier verifier = JWT
                    .require(algorithm)
                    .build();

            jwt = verifier.verify(token);
        } catch (TokenExpiredException e) {
            // 유효기간이 만료될 시 401에러 발생
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return Optional.ofNullable(jwt);
    }

    // 받은 refresh token이 access token을 갱신할 수 있는지 검사
    public boolean canTokenBeRefreshed(String token) {
        // 유효성 검증
        DecodedJWT decodedJWT = isValidRefreshToken(token)
                .orElseThrow(() -> new NoSuchElementException("유효한 토큰이 아닙니다."));

        // 유효기간 검증
        Date expiration = decodedJWT.getExpiresAt();

        return !expiration.before(new Date());
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
