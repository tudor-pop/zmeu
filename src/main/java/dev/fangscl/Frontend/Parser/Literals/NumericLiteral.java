package dev.fangscl.Frontend.Parser.Literals;

import dev.fangscl.Frontend.Parser.NodeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.text.DecimalFormat;

/*
 * NumericLiteral
 *      : NUMBER
 *      ;
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NumericLiteral extends Literal {
    private static DecimalFormat df = new DecimalFormat("#######.#######");
    private Number value;

    private NumericLiteral() {
    }

    public NumericLiteral(double value) {
        setDecimal(value);
    }

    public NumericLiteral(float value) {
        setDecimal(value);
    }

    private void setDecimal(float value) {
        setDecimal((double) value);
    }

    private void setDecimal(double value) {
        this.kind = NodeType.DecimalLiteral;
        this.value = Double.valueOf(df.format(value));
    }

    public NumericLiteral(int value) {
        setInteger(value);
    }

    private void setInteger(int value) {
        this.kind = NodeType.IntegerLiteral;
        this.value = value;
    }

    public NumericLiteral(String value) {
        setValue(value);
    }

    public NumericLiteral(Object value) {
        if (value instanceof String s) {
            setValue(s);
        } else if (value instanceof Integer i) {
            setInteger(i);
        } else if (value instanceof Double i) {
            setDecimal(i);
        } else if (value instanceof Float i) {
            setDecimal(i);
        }
    }

    public static NumericLiteral of(Object value) {
        return new NumericLiteral(value);
    }

    public static NumericLiteral of(int value) {
        return new NumericLiteral(value);
    }

    private void setValue(String value) {
        if (value.indexOf('.') != -1) { // string contains . => is a float/double
            this.value = Double.parseDouble(value);
            this.kind = NodeType.DecimalLiteral;
        } else {
            this.value = Integer.parseInt(value);
            this.kind = NodeType.IntegerLiteral;
        }
    }

    public boolean isDecimal() {
        return this.kind == NodeType.DecimalLiteral;
    }

    public boolean isInteger() {
        return this.kind == NodeType.IntegerLiteral;
    }

    @Override
    public String toSExpression() {
        return String.valueOf(value);
    }


    @Override
    public Object getVal() {
        return value;
    }
}
