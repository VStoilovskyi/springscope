package org.springscope;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springscope.scope.DefaultHeaderBasedScopeExtractor;
import org.springscope.scope.ScopeExtractor;

@Configuration
@EnableAspectJAutoProxy
public class SpringScopeAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(ScopeExtractor.class)
    public ScopeExtractor scopeExtractor(HttpServletRequest httpServletRequest ) {
    return new DefaultHeaderBasedScopeExtractor(httpServletRequest);
    }



}
