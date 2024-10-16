package org.springscope.auth;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springscope.scope.ScopeExtractor;

@Aspect
@Component
public class HasPermissionAspect {

  private final ScopeExtractor scopeExtractor;

  public HasPermissionAspect(ScopeExtractor scopeExtractor) {
    this.scopeExtractor = scopeExtractor;
  }

  @Around("@annotation(org.springscope.auth.HasPermission)")
  public Object handleHasPermissionAspect(ProceedingJoinPoint pjp) throws Throwable {
    MethodSignature signature = (MethodSignature) pjp.getSignature();
    Method method = signature.getMethod();
    HasPermission annotation = method.getAnnotation(HasPermission.class);
    if (annotation == null) {

      return pjp.proceed();
    }
    String rule = annotation.value();
    if (rule.contains("#")) {
      rule = injectMethodArgValues(rule, method.getParameters(), pjp.getArgs());
    }

    List<String> requestScopes = scopeExtractor.extractScopes();
    if (requestScopes.contains(rule)) {
      return pjp.proceed();
    }

    return ResponseEntity.status(403).body(Map.of());
  }

  private String injectMethodArgValues(String rule, Parameter[] parameters, Object[] argValues) {
    String localRule = rule;
    int argStartIdx = -1;
    Map<String, Integer> argNameToIndexMap = getArgNameToIndexMap(parameters);

    for (int i = 0; i < rule.length(); i++) {
      if (Character.toString(rule.charAt(i)).equals("#")) {
        argStartIdx = i;
        continue;
      }
      if (argStartIdx == -1) {
        continue;
      }

      Pattern pattern = Pattern.compile("[a-zA-Z ]");
      Matcher matcher = pattern.matcher(Character.toString(rule.charAt(i)));
      boolean isLetter = matcher.find();
      if (isLetter && i != rule.length() - 1) {
        continue;
      }
      String substrToReplace;
      if (i != rule.length() - 1) {
        substrToReplace = rule.substring(argStartIdx, i);
      } else {
        substrToReplace = rule.substring(argStartIdx);
      }

      Integer parameterIdx = argNameToIndexMap.get(substrToReplace.replace("#", ""));
      if (parameterIdx == null) {
        throw new RuntimeException(String.format("Cannot find param %s", substrToReplace));
      }

      /*
      * Todo: if introduce get to instance properties like `#user.getName()` should be added new logic
      * leave `toString()` for now to keep it simple at this time.
      Class<?> type = parameters[parameterIdx].getType();
      Object argValue = type.cast(argValues[parameterIdx]);
      */

      /*
      Params and args arrays have same order, so it is safe to get value's index by param name and
      then retrieve argument value by found index.
      */
      String argValue = argValues[parameterIdx].toString();
      localRule = localRule.replace(substrToReplace, argValue);
      argStartIdx = -1;
    }
    return localRule;
  }

  private Map<String, Integer> getArgNameToIndexMap(Parameter[] parameters) {
    Map<String, Integer> result = new HashMap<>();
    for (int i = 0; i < parameters.length; i++) {
      Parameter param = parameters[i];
      result.put(param.getName(), i);
    }
    return result;
  }
}
