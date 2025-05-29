package io.zmeu.api.annotations;

import io.zmeu.api.schema.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
    String name() default "";

    Type type();

    String description() default "";

    String deprecationMessage() default "";

    boolean optional() default true;

    boolean immutable() default false;

    boolean recreateOnChange() default false;

    boolean hidden() default false;

}
