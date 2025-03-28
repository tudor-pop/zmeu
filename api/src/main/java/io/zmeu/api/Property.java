package io.zmeu.api;

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
    boolean readonly() default false;

    boolean hidden() default false;

    enum Type {
        String("String"), Number("Number"), Boolean("Boolean"), Null("null");
        private final String value;

        Type(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

}
