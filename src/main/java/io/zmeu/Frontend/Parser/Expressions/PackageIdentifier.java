package io.zmeu.Frontend.Parser.Expressions;

import io.zmeu.Frontend.Parser.Literals.Identifier;
import io.zmeu.Frontend.Parser.NodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A path is a sequence of one or more path segments logically separated by a package qualifier (.)
 * If a path has multiple segments it always refers to an item. If there are no multiple segments it refers to an
 * item in the local scope.
 * This usually holds a package name when importing a package or when using a schema/resource
 * resource Std.Files@2022-01-01 { ... }
 * SimplePathExpression:
 *  Identifier.?(Identifier.)*(@yyyy-mm-dd)?
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
public class PackageIdentifier extends Identifier {
    @Builder.Default
    private List<String> packageName = new ArrayList<>();
    private LocalDate date;

    public PackageIdentifier() {
        this.kind = NodeType.SimplePathExpression;
        this.packageName = new ArrayList<>();
    }

    private PackageIdentifier(String type, LocalDate date) {
        this();
        this.setSymbol(type);
        this.date = date;
    }

    public static Expression of(String object, LocalDate property) {
        return new PackageIdentifier(object, property);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }

    public void addPackage(String value) {
        this.packageName.add(value);
    }

}
