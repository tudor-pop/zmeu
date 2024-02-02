package io.zmeu.api;

public @interface Schema {
    String description();

    String typeName();

    String uri() default "";
    String version() default "0.0.1";

}
