package io.zmeu.Frontend.Parser.Literals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.zmeu.Frontend.Parser.Types.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * A path is a sequence of one or more path segments logically separated by a package qualifier (.)
 * If a path has multiple segments it always refers to an item. If there are no multiple segments it refers to an
 * item in the local scope.
 * This usually holds a package name when importing a package or when using a schema/resource
 * PathIdentifier:
 * ; .?Identifier(.Identifier)*
 */
//@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true )
@Data
@Builder
@AllArgsConstructor
public class TypeIdentifier extends Identifier {
    private Type type;

    public TypeIdentifier() {
        super();
    }

    private TypeIdentifier(String path, Type type) {
        this();
        setSymbol(path);
        setType(type);
    }


    private TypeIdentifier(String path, String type) {
        this(path, Type.of(type));
    }

    private TypeIdentifier(String path) {
        this(path, Type.of(path));
    }

    public static TypeIdentifier type(String type) {
        return of(type);
    }

    public static TypeIdentifier type(Type type) {
        return new TypeIdentifier(type);
    }

    public static TypeIdentifier of(String object) {
        if (object.indexOf('.') == -1) {
            return new TypeIdentifier(object);
        }
        var prefix = StringUtils.substringBeforeLast(object, ".");
        var type = StringUtils.substringAfterLast(object, ".");
        return new TypeIdentifier(prefix, type);
    }

    public static TypeIdentifier from(String type) {
        var split = type.split("@");
        return fromParent(split[0]);
    }

    private static TypeIdentifier fromParent(String type) {
        var sub = type.substring(type.lastIndexOf('.') + 1, type.length());
        return new TypeIdentifier(type, Type.of(sub));
    }

    @JsonIgnore
    @Override
    public String symbolWithType() {
        return getSymbol() + type.value();
    }

}
