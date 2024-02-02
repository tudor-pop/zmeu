package io.zmeu.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface Property {
    String name() default "";

    String type();

    String description() default "";

    String deprecationMessage() default "";

    boolean optional() default true;
    boolean readonly() default false;

    boolean hidden() default false;

}
