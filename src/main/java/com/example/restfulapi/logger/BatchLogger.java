package com.example.restfulapi.logger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

/**
 * ログ出力内容を定義するログクラス
 *
 * @author Natsume Takuya
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BatchLogger implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    long start = System.currentTimeMillis();

    chain.doFilter(request, response);

    long end = System.currentTimeMillis();
    HttpServletRequest httpRequest = ((HttpServletRequest) request);
    HttpServletResponse httpResponse = ((HttpServletResponse) response);

    String method = httpRequest.getMethod();
    String url = httpRequest.getRequestURI();
    int status = httpResponse.getStatus();
    int executionTime = (int) (end - start);

    log.info("{}\t{}\t{}\t{}\t{}", executionTime, method, url, status, LocalDate.now());
  }
}
