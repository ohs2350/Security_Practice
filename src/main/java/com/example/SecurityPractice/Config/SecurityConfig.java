package com.example.SecurityPractice.Config;

import com.example.SecurityPractice.security.common.FilterSkipMatcher;
import com.example.SecurityPractice.security.filter.FormLoginFilter;
import com.example.SecurityPractice.security.filter.JwtAuthenticationFilter;
import com.example.SecurityPractice.security.handler.CustomLogoutHandler;
import com.example.SecurityPractice.security.handler.CustomLogoutSuccessHandler;
import com.example.SecurityPractice.security.handler.FormLoginAuthenticationFailureHandler;
import com.example.SecurityPractice.security.handler.FormLoginAuthenticationSuccessHandler;
import com.example.SecurityPractice.security.jwt.HeaderTokenExtractor;
import com.example.SecurityPractice.security.jwt.JwtDecoder;
import com.example.SecurityPractice.security.jwt.JwtFactory;
import com.example.SecurityPractice.security.jwt.RedisService;
import com.example.SecurityPractice.security.provider.FormLoginAuthenticationProvider;
import com.example.SecurityPractice.security.provider.JWTAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.ArrayList;
import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private FormLoginAuthenticationSuccessHandler formLoginAuthenticationSuccessHandler;
    @Autowired
    private FormLoginAuthenticationFailureHandler formLoginAuthenticationFailureHandler;
    @Autowired
    private FormLoginAuthenticationProvider provider;
    @Autowired
    private JWTAuthenticationProvider jwtProvider;
    @Autowired
    private HeaderTokenExtractor headerTokenExtractor;
    @Autowired
    private RedisService redisService;
    @Autowired
    JwtDecoder jwtDecoder;


    // 1. 사용자를 검사하는 특정 주소와 인증 성공&실패 핸들러를 담아서 formLoginFilter 메서드를 생성
    protected FormLoginFilter formLoginFilter() throws Exception {
        FormLoginFilter filter = new FormLoginFilter(
                new AntPathRequestMatcher("/api/user/login", HttpMethod.POST.name()),
                formLoginAuthenticationSuccessHandler,
                formLoginAuthenticationFailureHandler
        );
        // AuthenticationManager 등록
        filter.setAuthenticationManager(authenticationManager());

        return filter;
    }

    // AuthenticationManager 빈 등록
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        List<AuthenticationProvider> authenticationProviderList = new ArrayList<>();
        authenticationProviderList.add(provider);
        authenticationProviderList.add(jwtProvider);
        ProviderManager authenticationManager = new ProviderManager(authenticationProviderList);

        return authenticationManager;
    }

     // 2. provider 등록
     @Autowired
     public void config(AuthenticationManagerBuilder builder) {
         builder.authenticationProvider(this.provider)
                 .authenticationProvider(this.jwtProvider);
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
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .headers()
                .frameOptions()
                .disable();
//        http
//                .csrf()
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());// HttpOnly 적용
//        http
//                .rememberMe()
//                .key("uniqueAndSecret")
//                .rememberMeCookieName("remember-me-cookie")
//                .tokenValiditySeconds(86400).useSecureCookie(true); // Secure 적용


//                // 로그인, 회원가입 API 는 토큰이 없는 상태에서 요청이 들어오기 때문에 permitAll 설정
//                .and()
//                .authorizeRequests() // http servletRequest 를 사용하는 요청들에 대한 접근제한을 설정
//                .antMatchers("/index", "/", "/userLogin", "/api/**")
//                .permitAll()
//
//                // 나머지 API 는 전부 인증 필요
//                .anyRequest().authenticated()
//
//                .and()
//        http
//                .formLogin()
//                .loginPage("/login")
//                .permitAll();

        // formLoginFilter, jwt 필터 등록
        http
                .addFilterBefore(
                        formLoginFilter(),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(
                        jwtFilter(),
                        UsernamePasswordAuthenticationFilter.class
                );

        // GET : /api/user 에 대하여 권한이 필요한 접근 설정
        http
                .authorizeRequests()
                .mvcMatchers(
                        HttpMethod.GET,
                        "/api/user"
                )
                .hasRole("USER");
        http
                .authorizeRequests()
                .mvcMatchers(
                        HttpMethod.GET,
                        "/hello"
                )
                .hasRole("USER");

        // 로그아웃 설정
        http.logout() // 로그아웃 기능 작동함
                .logoutUrl("/logout") // 로그아웃 처리 URL, default: /logout, 원칙적으로 post 방식만 지원
                .logoutSuccessUrl("/index") // 로그아웃 성공 후 이동페이지
                .addLogoutHandler(customLogoutHandler()) // 로그아웃 헨들러 등록
                .logoutSuccessHandler(customLogoutSuccessHandler()) // 로그아웃 성공 핸들러
                .deleteCookies("ACCESS_TOKEN");

                // JwtFilter 를 등록한다.
                // UsernamePasswordAuthenticationFilter 앞에 등록하는 이유는 딱히 없지만
                // SecurityContext를 사용하기 때문에 앞단의 필터에서 SecurityContext가 설정되고 난뒤 필터를 둔다.
                //.and()
                //.addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    private JwtAuthenticationFilter jwtFilter() throws Exception {
        List<AntPathRequestMatcher> skipPath = new ArrayList<>();

        // Static 정보 접근 허용
        skipPath.add(new AntPathRequestMatcher("/error", HttpMethod.GET.name()));
        skipPath.add(new AntPathRequestMatcher("/favicon.ico", HttpMethod.GET.name()));
        skipPath.add(new AntPathRequestMatcher("/static", HttpMethod.GET.name()));
        skipPath.add(new AntPathRequestMatcher("/static/**", HttpMethod.GET.name()));

        skipPath.add(new AntPathRequestMatcher("/api/user", HttpMethod.POST.name()));
        skipPath.add(new AntPathRequestMatcher("/api/user/login", HttpMethod.POST.name()));
        skipPath.add(new AntPathRequestMatcher("/login", HttpMethod.GET.name()));
        skipPath.add(new AntPathRequestMatcher("/logout", HttpMethod.POST.name()));
        skipPath.add(new AntPathRequestMatcher("/", HttpMethod.GET.name()));
        skipPath.add(new AntPathRequestMatcher("/index", HttpMethod.GET.name()));
        skipPath.add(new AntPathRequestMatcher("/refresh", HttpMethod.POST.name()));

        FilterSkipMatcher matcher = new FilterSkipMatcher(
                skipPath,
                "/**"
        );

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                matcher,
                headerTokenExtractor
        );
        // AuthenticationManager 등록
        filter.setAuthenticationManager(authenticationManager());

        return filter;
    }


    private CustomLogoutHandler customLogoutHandler() {
        return new CustomLogoutHandler(headerTokenExtractor, redisService, jwtDecoder);
    }

    private CustomLogoutSuccessHandler customLogoutSuccessHandler() {
        return new CustomLogoutSuccessHandler();
    }
}
