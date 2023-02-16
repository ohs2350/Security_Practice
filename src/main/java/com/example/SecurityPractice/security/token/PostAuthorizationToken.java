package com.example.SecurityPractice.security.token;

import com.example.SecurityPractice.DTO.UserDTO;
import com.example.SecurityPractice.DTO.UserJwtDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PostAuthorizationToken extends UsernamePasswordAuthenticationToken {
    // 인증 후 토큰

    /**
     * 1. Object principal - 주도자, 주역, 사용자 본인을 의미 간단하게 유저 아이디 생각
     * 2. Object credentials - 자격 인증서, 자격증
     * Principal 에서 사용자 본인을 의미한다면 Credentials 은 그에 따른 자격을 증명하고자 할 때 사용.
     * Principal 과 마찬가지로 구현에 따라 어떠한 정보가 들어갈지 달라지지만 대체적으로 암호화된 정보를 저장하며, 보안에 신경을 많이 써야 되는 정보
     * Collection<? extends GrantedAuthority> authorities - 권한을 List 형태로 받기
     *  - 사용자의 지정한 권한 범위를 기술하기 위해 추상화된 클래스 (권한이 Role 만 있는것이 아니기에 List 형태로 받는다. 여러 가지 조건으로 제한이 가능하다는 것)
     * */
    private PostAuthorizationToken(
            Object principal,
            Object credentials,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(principal, credentials, authorities);
    }

    public static PostAuthorizationToken getTokenFormUserDetails(UserDetails userDetails) {

        return new PostAuthorizationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
    }

    public static PostAuthorizationToken getTokenFormUserDetails(UserJwtDTO userJwtDTO) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(
                new SimpleGrantedAuthority(userJwtDTO.getRole().toString())
        );

        return new PostAuthorizationToken(
                userJwtDTO,
                "null password",
                grantedAuthorities
        );
    }

    public UserDetails getUserDetails() {

        return (UserDetails) super.getPrincipal();
    }
}
