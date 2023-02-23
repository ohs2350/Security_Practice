package com.example.SecurityPractice.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * addViewControllers - 컨트롤러를 거치지않고 바로 페이지 호출
     * */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/hello").setViewName("hello");
        registry.addViewController("/login").setViewName("login");
    }

    // CORS(Cross-Origin Resource Sharing) 설정
    // 보안상의 이유로 웹 애플리케이션에서 다른 도메인으로 리소스를 요청할 때 발생할 수 있는 보안 문제를 해결하기 위한 매커니즘
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
//                .allowedOrigins("*")
                .allowedOriginPatterns("*") // 요청을 허용할 도메인
                .allowedMethods("*") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 허용할 HTTP 헤더
                .allowCredentials(true) // 쿠키를 사용하여 인증하는 경우 true로 설정
                .maxAge(3600); // 브라우저가 preflight request를 캐시할 시간(초)을 설정
    }


}
