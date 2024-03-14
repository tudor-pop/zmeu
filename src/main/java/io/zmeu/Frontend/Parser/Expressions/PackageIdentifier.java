package io.zmeu.Frontend.Parser.Expressions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.NodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * A path is a sequence of one or more path segments logically separated by a package qualifier (.)
 * If a path has multiple segments it always refers to an item. If there are no multiple segments it refers to an
 * item in the local scope.
 * This usually holds a package name when importing a package or when using a schema/resource
 * SimplePathExpression:
 * (Identifier.)+
 */
//@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true )
@Data
@Builder
@AllArgsConstructor
public class PackageIdentifier extends Identifier {
    @Builder.Default
    private StringBuilder packageName = new StringBuilder();

    public PackageIdentifier() {
        this.kind = NodeType.SimplePathExpression;
        this.packageName = new StringBuilder();
    }

    private PackageIdentifier(String type) {
        this();
        setSymbol(type);
    }

    private PackageIdentifier(String typePrefix, String type) {
        this();
        addPackage(typePrefix);
        setSymbol(type);
    }

    public void setType(String type) {
        setSymbol(type);
    }

    public static PackageIdentifier of(String object) {
        var prefix = StringUtils.substringBeforeLast(object, ".");
        var type = StringUtils.substringAfterLast(object, ".");
        return new PackageIdentifier(prefix, type);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }

    public void addPackage(String value) {
        packageName.append(value);
    }

    @JsonIgnore
    public String packageNameString() {
        return packageName.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        PackageIdentifier that = (PackageIdentifier) object;
        return StringUtils.equals(getPackageName(), that.getPackageName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPackageName());
    }
}
