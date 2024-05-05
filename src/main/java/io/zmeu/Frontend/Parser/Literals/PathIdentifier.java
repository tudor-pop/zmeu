package io.zmeu.Frontend.Parser.Literals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.zmeu.Frontend.Parser.NodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
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
    private StringBuilder provider = new StringBuilder();
    private LocalDate date;

    public PathIdentifier() {
        this.kind = NodeType.Identifier;
        this.provider = new StringBuilder();
    }

    private PathIdentifier(String provider) {
        this();
        setSymbol(provider);
    }

    private PathIdentifier(String provider, String type) {
        this();
        addPackage(provider);
        setSymbol(type);
    }

    public void setType(String type) {
        setSymbol(type);
    }

    public void addPackage(String value) {
        provider.append(value);
    }

    public static PathIdentifier of(String object) {
        return from(object);
//        if (object.indexOf('.') == -1) {
//            return new PathIdentifier(object);
//        }
//        var prefix = StringUtils.substringBeforeLast(object, ".");
//        var type = StringUtils.substringAfterLast(object, ".");
//        return new PathIdentifier(prefix, type);
    }

    public static PathIdentifier from(String type) {
        var split = type.split("@");
        PathIdentifier pathIdentifier = fromParent(split[0]);
        if (split.length == 2) {
            pathIdentifier.setDate(split[1]);
        }
        return pathIdentifier;
    }

    private static PathIdentifier fromParent(String type) {
        var split = type.split("/");
        if (split.length > 1) { // PluginName.Module/resource@date
            return new PathIdentifier(split[0], split[1]);
        }
        return new PathIdentifier(split[0]);
    }


    public void setDate(String date) {
        this.date = LocalDate.parse(date);
    }

    @JsonIgnore
    @Override
    public String symbolWithType() {
        return provider.toString() + getSymbol();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (PathIdentifier) object;
        return StringUtils.equals(getProvider(), that.getProvider()) && StringUtils.equals(super.getSymbol(), that.getSymbol());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getProvider());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PathIdentifier.class.getSimpleName() + "[", "]")
                .add("path=" + provider + getSymbol())
                .add("kind=" + kind)
                .toString();
    }
}
