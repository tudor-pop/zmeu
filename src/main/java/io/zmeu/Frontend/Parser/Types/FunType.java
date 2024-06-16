package io.zmeu.Frontend.Parser.Types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
