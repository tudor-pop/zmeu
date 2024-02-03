package io.zmeu.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
    String name() default "";

    String type();

    String description() default "";

    String deprecationMessage() default "";

    boolean optional() default true;
    boolean readonly() default false;

    boolean hidden() default false;

}
