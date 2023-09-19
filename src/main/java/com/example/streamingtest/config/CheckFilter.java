package com.example.streamingtest.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class CheckFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    //log.info("!!! Filter !!!");
    //log.info("request.getHeaderNames() = " + request.getHeaderNames());
    //log.info("request.getRequestURI() = " + request.getRequestURI());
    log.info("request.getRequestURL() = " + request.getRequestURL());
    //log.info("request.getPathInfo() = " + request.getPathInfo());
    //log.info("request.getQueryString() = " + request.getQueryString());
    //log.info("request.getContentType() = " + request.getContentType());
    filterChain.doFilter(request, response);
  }
}
