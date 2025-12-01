package com.glidingpath.auth.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.lang.annotation.*;
 
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal(expression = "@fetchUser.apply(#this)", errorOnInvalidType = true)
public @interface CurrentUser {} 