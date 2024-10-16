package org.springscope.scope;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
//@RequiredArgsConstructor
public class DefaultHeaderBasedScopeExtractor implements ScopeExtractor {

  private final HttpServletRequest httpServletRequest;

  public DefaultHeaderBasedScopeExtractor(HttpServletRequest httpServletRequest) {
    this.httpServletRequest = httpServletRequest;
  }

  @Override
  public List<String> extractScopes() {
    String header = httpServletRequest.getHeader("x-scopes");
    if (header == null) {
      return List.of();
    }
    return Arrays.stream(header.split(" ")).toList();
  }
}
