package com.example.SecurityPractice.security.provider;

import com.example.SecurityPractice.DTO.UserDTO;
import com.example.SecurityPractice.DTO.UserJwtDTO;
import com.example.SecurityPractice.security.jwt.JwtDecoder;
import com.example.SecurityPractice.security.token.JwtPreProcessingToken;
import com.example.SecurityPractice.security.token.PostAuthorizationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JWTAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private JwtDecoder jwtDecoder;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String token = (String) authentication.getPrincipal();
        UserJwtDTO userJwtDTO = jwtDecoder.decodeJwt(token);

        return PostAuthorizationToken.getTokenFormUserDetails((UserDetails) userJwtDTO);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtPreProcessingToken.class.isAssignableFrom(authentication);
    }
}
