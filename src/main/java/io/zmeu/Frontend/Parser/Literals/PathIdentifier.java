package io.zmeu.Frontend.Parser.Literals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.zmeu.Frontend.Parser.Expressions.Visitor;
import io.zmeu.Frontend.Parser.NodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.StringJoiner;

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
public class PathIdentifier extends Identifier {
    @Builder.Default
    private StringBuilder path = new StringBuilder();

    public PathIdentifier() {
        this.kind = NodeType.Path;
        this.path = new StringBuilder();
    }

    private PathIdentifier(String path) {
        this();
        setSymbol(path);
    }

    private PathIdentifier(String path, String type) {
        this();
        addPackage(path);
        setSymbol(type);
    }

    public void setType(String type) {
        setSymbol(type);
    }

    public static PathIdentifier of(String object) {
        if (object.indexOf('.') == -1) {
            return new PathIdentifier(object);
        }
        var prefix = StringUtils.substringBeforeLast(object, ".");
        var type = StringUtils.substringAfterLast(object, ".");
        return new PathIdentifier(prefix, type);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }

    public void addPackage(String value) {
        path.append(value);
    }

    @JsonIgnore
    @Override
    public String symbolWithType() {
        return path.toString() + getSymbol();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (PathIdentifier) object;
        return StringUtils.equals(getPath(), that.getPath()) && StringUtils.equals(super.getSymbol(), that.getSymbol());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPath());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PathIdentifier.class.getSimpleName() + "[", "]")
                .add("path=" + path + getSymbol())
                .add("kind=" + kind)
                .toString();
    }
}
