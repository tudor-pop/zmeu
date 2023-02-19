package dev.fangscl.Frontend.Parser.Literals;

import dev.fangscl.Frontend.Parser.NodeType;
import dev.fangscl.Runtime.TypeSystem.Base.Expression;
import lombok.Data;
import lombok.EqualsAndHashCode;

/*
 * NumericLiteral
 *      : NUMBER
 *      ;
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NumericLiteral extends Expression {
    private Number value;

    private NumericLiteral() {
    }

    public NumericLiteral(double value) {
        this.kind = NodeType.DecimalLiteral;
        this.value = value;
    }

    public NumericLiteral(float value) {
        this.kind = NodeType.DecimalLiteral;
        this.value = value;
    }

    public NumericLiteral(int value) {
        this.kind = NodeType.IntegerLiteral;
        this.value = value;
    }

    public NumericLiteral(String value) {
        if (value.indexOf('.') != -1) { // string contains . => is a float/double
            this.value = Double.parseDouble(value);
            this.kind = NodeType.DecimalLiteral;
        } else {
            this.value = Integer.parseInt(value);
            this.kind = NodeType.IntegerLiteral;
        }
    }

}
