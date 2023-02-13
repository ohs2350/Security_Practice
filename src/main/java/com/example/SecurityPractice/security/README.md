[출처] : https://github.com/alalstjr/Java-spring-boot-security-jwt

## 인증 진행 순서 (로그인)

- 1. SecurityConfig
- 2. FormLoginFilter (attemptAuthentication 인증 시도)
- 3. PreAuthorizationToken
- 4. FormLoginAuthenticationProvider
- 5. PostAuthorizationToken
- 6. successfulAuthentication
- 7. FormLoginAuthenticationSuccessHandler
- 8. JwtFactory
### 1. SecurityConfig

configure 메소드는 `인증을 담당할 프로바이더 구현체를 설정, 필터 등록을 하는 메소드`이다.  
WebSecurityConfigurerAdapter 추상 클래스를 상속 받는다.  
`스프링 자동 보안 구성을 건너뛰고 사용자정의 보안구성`하기 위해서 상속받는 클래스

반대되는 전역을 보안하는 상속 클래스 `GlobalAuthenticationConfigurerAdapter` 존재합니다.

- 가장 먼저 인증이 필요한 서버에 `사용자가 접속시 가장 처음 Filter를 연결`해주는 역할


### 2. FormLoginFilter

<a href="https://docs.spring.io/spring-security/site/docs/4.2.12.RELEASE/apidocs/org/springframework/security/web/authentication/AbstractAuthenticationProcessingFilter.html" target="_blank">AbstractAuthenticationProcessingFilter DOCS</a>

- `AbstractAuthenticationProcessingFilter 추상 클래스`
    - 웹 기반 인증 요청에 사용. 폼 POST, SSO 정보 또는 기타 사용자가 제공한 `크리덴셜(크리덴셜은 사용자가 본인을 증명하는 수단)`을 포함한 요청을 처리.
    - 브라우저 기반 HTTP 기반 인증 요청 에서 사용되는 컴포넌트로 POST 폼 데이터를 포함하는 요청을 처리한다.
    - `인증 실패와 인증 성공 관련 이벤트를 관련 핸들러 메서드`를 가지고 있습니다.
    - 사용자 비밀번호를 다른 필터로 전달하기 위해서 `Authentication 객체를 생성하고 일부 프로퍼티를 설정`한다.

간단하게 설명하자면 인증요청에 해당하는 URL 을 감지하면 최초로 `AbstractAuthenticationProcessingFilter 를 구현한 클래스(FormLoginFilter)가 요청을 가로챈 후 Authentication 객체를 생성`한다.  
AbstractAuthenticationProcessingFilter 클래스의 `doFilter 메서드로 인해서 가장 처음 인증 attemptAuthentication 메서드를 실행`합니다.