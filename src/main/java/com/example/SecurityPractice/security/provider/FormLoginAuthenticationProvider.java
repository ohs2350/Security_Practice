package com.example.SecurityPractice.security.provider;

import com.example.SecurityPractice.Service.UserService;
import com.example.SecurityPractice.security.token.PostAuthorizationToken;
import com.example.SecurityPractice.security.token.PreAuthorizationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class FormLoginAuthenticationProvider implements AuthenticationProvider {
    // 로그인한 사용자의 인증 권한을 검사합니다.
    // AuthenticationProvider 인터페이스를 상속 - 특정 Authentication 구현을 처리 할 수있는 클래스를 나타냄
    // 화면에서 입력한 로그인 정보와 DB에서 가져온 사용자의 정보를 비교해주는 인터페이스
    // 해당 인터페이스에 오버라이드되는 authenticate() 메서드는 화면에서 사용자가 입력한 로그인 정보를 담고 있는 Authentication 객체를 가지고 있다.
    // 해당 인터페이스는 인증에 성공하면 인증된 Authentication 객체를 생성하여 리턴하기 때문에 비밀번호, 계정 활성화, 잠금 모든 부분에서 확인이 되었다면 리턴

    @Autowired
    private UserService userService;

    // 4.
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 1. 로그인한 사용자와 DB 사용자를 비교하는 메서드 authenticate() @Override
     * */
    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        /**
         * 2.
         * 기본적으로 이전에 받은 로그인 정보를 담고 있는 Authentication 객체 PreToken 값을 가지고 있습니다.
         * 해당 PreToken 에서 로그인한 유저의 정보를 변수에 담습니다.
         * */
        PreAuthorizationToken token = (PreAuthorizationToken) authentication;

        String username = token.getUsername();
        String password = token.getUserPassword();

        // 3. 로그인한 유저가 DB에 존재하는지 service 를 통해서 확인.
        UserDetails user = userService.loadUserByUsername(username);
        System.out.println("테스트" + username);
        /**
         * 4.
         * 로그인한 유저와 DB에 존재하는 유저의 Password 가 동일한지 조회하는 메소드 (*무조건 비교 대상이 앞에 와야한다.)
         * 로그인한 유저가 DB에 존재한다면 PostAuthorizationToken(권한이 부여된 토큰) 객체를 생성하여 return
         * */
        if (isCorrectPassword(password, user.getPassword())) {
            return PostAuthorizationToken
                    .getTokenFormUserDetails(user);
        }

        // 이곳까지 통과하지 못하면 잘못된 요청으로 접근하지 못한것, 그러므로 throw
        throw new NoSuchElementException("인증 정보가 정확하지 않습니다.");
    }

    /**
     * 5.
     * AuthenticationProvider 인터페이스가 지정된 Authentication 객체를 지원하는 경우에 true 를 리턴한다.
     * form action 진행 시 해당 클래스의 supports() > authenticate() 순으로 인증 절차 진행
     * 사용자 인증이 완료되면 사용자에게 권한을 부여한 토큰을 생성하여 return
     * */
    @Override
    public boolean supports(Class<?> authentication) {
        // Security 에 Filter 에서 사용한 PreAuthorizationToken 를 참고해서 여러곳을 검색하여 해당 Provider 로 연결
        return PreAuthorizationToken.class.isAssignableFrom(authentication);
    }

    // 4.
    private boolean isCorrectPassword(String password, String accountPassword) {
        return passwordEncoder.matches(password, accountPassword);
    }
}
