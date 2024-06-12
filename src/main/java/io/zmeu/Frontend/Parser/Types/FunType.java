package io.zmeu.Frontend.Parser.Types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public final class FunType extends Type {
    private List<Type> params;
    private Type returnType;

    public FunType(List<Type> params, Type returnType) {
        super();
        this.params = params;
        this.returnType = returnType;
        setValue(name());
    }

    public static FunType valueOf(@NotBlank String symbol) {
        var funSplit = StringUtils.split(symbol, "->");
        Type returnType = null;
        List<Type> paramsType = new ArrayList<>();
        if (funSplit.length == 2) {
            returnType = Type.fromString(funSplit[1]);
            paramsType = cleanParams(funSplit[0]);
        } else if (funSplit.length == 1) {
            paramsType = cleanParams(funSplit[0]);
        }
        return new FunType(paramsType, returnType);
    }

    private static List<Type> cleanParams(java.lang.String funSplit) {
        var split = StringUtils.substringBetween(funSplit,"(",")").split(",");
        return Arrays.stream(split)
                .map(Type::fromString)
                .collect(Collectors.toList());
    }

    public String name() {
        if (this.getValue() == null) {
            var value = "fun(" + StringUtils.join(params, ",") + ")" + returnType();
            setValue(value);
        }
        return getValue();
    }

    private @NotNull String returnType() {
        if (returnType == null) {
            return "";
        }
        return this.returnType.hasValue() ? ":" + this.returnType.getValue() : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof FunType funType)) return false;

        return new EqualsBuilder()
                .append(getValue(), funType.getValue())
                .append(getParams(), funType.getParams())
                .append(getReturnType(), funType.getReturnType())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getValue())
                .append(getParams())
                .append(getReturnType())
                .toHashCode();
    }

    public String getName() {
        return name();
    }

}
