package com.example.SecurityPractice.Config;

import com.example.SecurityPractice.security.filter.FormLoginFilter;
import com.example.SecurityPractice.security.handler.FormLoginAuthenticationFailureHandler;
import com.example.SecurityPractice.security.handler.FormLoginAuthenticationSuccessHandler;
import com.example.SecurityPractice.security.provider.FormLoginAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private FormLoginAuthenticationSuccessHandler formLoginAuthenticationSuccessHandler;
    @Autowired
    private FormLoginAuthenticationFailureHandler formLoginAuthenticationFailureHandler;
    @Autowired
    private FormLoginAuthenticationProvider provider;


    // 1. 사용자를 검사하는 특정 주소와 인증 성공&실패 핸들러를 담아서 formLoginFilter 메서드를 생성합니다.
    //addFilterBefore 필터 등록을 해줍니다.
    protected FormLoginFilter formLoginFilter() throws Exception {
        FormLoginFilter filter = new FormLoginFilter(
                new AntPathRequestMatcher("/api/account/login", HttpMethod.POST.name()),
                formLoginAuthenticationSuccessHandler,
                formLoginAuthenticationFailureHandler
        );
        // filter.setAuthenticationManager(super.authenticationManagerBean());

        return filter;
    }

    // 2. provider 등록 해줍니다.
    protected void configure(AuthenticationManagerBuilder auth) {
        auth
                .authenticationProvider(this.provider);
    }

//    public SecurityConfig(JwtConfig jwtConfig) {
//        this.jwtConfig = jwtConfig;
//    }

    // Spring Security 5 권장하는 인코더
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws
            Exception {
        http
                // CSRF 설정 Disable
                // session 기반 인증과는 다르게 stateless하기 때문에 서버에 인증정보를 보관x
                // 서버에 인증정보를 저장하지 않기 때문에 굳이 불필요한 csrf 코드들을 작성할 필요가 없다.
                .csrf().disable()

                // 시큐리티는 기본적으로 세션을 사용
                // 여기서는 세션을 사용하지 않기 때문에 세션 설정을 Stateless 로 설정
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 로그인, 회원가입 API 는 토큰이 없는 상태에서 요청이 들어오기 때문에 permitAll 설정
                .and()
                .authorizeRequests() // http servletRequest 를 사용하는 요청들에 대한 접근제한을 설정
                .antMatchers("/index", "/", "/userLogin", "/api/**")
                .permitAll()

                // 나머지 API 는 전부 인증 필요
                .anyRequest().authenticated()

                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll();

                // JwtFilter 를 등록한다.
                // UsernamePasswordAuthenticationFilter 앞에 등록하는 이유는 딱히 없지만
                // SecurityContext를 사용하기 때문에 앞단의 필터에서 SecurityContext가 설정되고 난뒤 필터를 둔다.
                //.and()
                //.addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
