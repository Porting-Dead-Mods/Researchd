package com.portingdeadmods.researchd.pdl.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ConfigValue {
    String key() default "";

    String category() default "";

    String name() default "";

    String comment() default "";

    double[] range() default {0, Integer.MAX_VALUE};

}
