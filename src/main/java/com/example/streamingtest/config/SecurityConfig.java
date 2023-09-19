package com.example.streamingtest.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

  private final CheckFilter checkFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .cors().disable()
        .csrf().disable()
        .httpBasic().disable()
        .formLogin().disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .antMatcher("/**")
        .authorizeRequests()
        .antMatchers(

            "/**"

        )
        .permitAll()//회원가입과 로그인은 인증이 없어야 한다.
        .anyRequest()
        .authenticated();

    // cors 필터 다음에 ExoPlayer의 hls파트가 호출하는 url을 체크하는 부분을 넣어줌.
    http.addFilterAfter(checkFilter, CorsFilter.class);

    return http.build();

  }
}
