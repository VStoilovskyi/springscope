package org.springscope.auth;


import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Annotation for guarding REST Controller's method. If added - value will be checked for presence
 * in the given scopes. If permission is not found then response with 403 Forbidden code will be
 * returned.
 * <pre class="code">
 * &#064;HasPermission("admin:read")
 * </pre>
 *
 * <p>Method's arguments can be retrieved by the mask "#argName" and presented in the string type,
 * if it is mentioned in the method signature eg:</p>
 * <pre class="code">
 *   &#064;HasPermission("#userid:read")
 *   &#064;GetMapping("/user/{userId}")
 *   public String getUserRead(@PathVariable String userId)
 * </pre>
 * In the example above value of the variable userId will be injected to string and validated if the
 * "scope" header's value contains this particular value.
 */
@Retention(RUNTIME)
@Target({METHOD})
public @interface HasPermission {

  String value();
}
