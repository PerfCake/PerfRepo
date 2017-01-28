package org.perfrepo.web.security;

import org.perfrepo.enums.AccessType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker of the method parameter against which we should do authorization.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthEntity {

    AccessType accessType() default AccessType.WRITE;

    String messageKey() default "";

    String[] messageArgs() default {};

}
