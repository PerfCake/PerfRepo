package org.jboss.qa.perfrepo.security;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;


@Retention(RUNTIME)
@Target({METHOD, TYPE})
@InterceptorBinding
public @interface Secure {}
