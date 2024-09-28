package io.zmeu.Frontend.Parser.Literals;

import io.zmeu.TypeChecker.Types.Type;
import io.zmeu.TypeChecker.Types.TypeFactory;
import io.zmeu.Visitors.Visitor;
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
 * ; PathIdentifier?.TypeIdentifier
 */
//@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true )
@Data
@Builder
@AllArgsConstructor
public final class TypeIdentifier extends Identifier {
    private Type type;
    private PathIdentifier path;

    private TypeIdentifier() {
        super();
    }

    @Override
    public String string() {
        if (path.getPaths().length == 1) {
            return type.getValue();
        }
        return path.string() + "." + type.getValue();
    }

    private TypeIdentifier(String path, Type type) {
        this();
        setPath(PathIdentifier.of(path));
        setType(type);
    }

    private TypeIdentifier(String path, String type) {
        this(path, TypeFactory.of(type));
    }

    private TypeIdentifier(String path) {
        this(path, TypeFactory.of(path));
    }

    private TypeIdentifier(Type type) {
        this();
        setType(type);
    }

    public static TypeIdentifier type(String type) {
        if (type.indexOf('.') == -1) {
            return new TypeIdentifier(type);
        }
        var prefix = StringUtils.substringBeforeLast(type, ".");
        var type1 = StringUtils.substringAfterLast(type, ".");
        return new TypeIdentifier(prefix, type1);
    }

    public static TypeIdentifier type(Type type) {
        return new TypeIdentifier(type);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
