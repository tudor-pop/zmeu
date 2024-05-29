package io.zmeu.Frontend.TypeChecker;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class FunType extends DataTypes {
    private List<DataTypes> params;
    private DataTypes returnType;

    public FunType(java.lang.String value, List<DataTypes> params, DataTypes returnType) {
        super(value);
        this.params = params;
        this.returnType = returnType;
        setValue(name());
    }

    public FunType(List<DataTypes> params, DataTypes returnType) {
        this.params = params;
        this.returnType = returnType;
    }

    public String name() {
        if (this.name == null) {
            name = "fun[" + StringUtils.join(params, ",") + "] :" + returnType.getValue();
        }
        return name;
    }
}
