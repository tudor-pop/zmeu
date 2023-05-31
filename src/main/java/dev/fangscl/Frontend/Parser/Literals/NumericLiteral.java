package dev.fangscl.Frontend.Parser.Literals;

import dev.fangscl.Frontend.Parser.Expressions.Expression;
import dev.fangscl.Frontend.Parser.Expressions.Visitor;
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

    private NumericLiteral(double value) {
        setDecimal(value);
    }

    private NumericLiteral(float value) {
        setDecimal(value);
    }

    private void setDecimal(float value) {
        setDecimal(Double.parseDouble(String.valueOf(value)));
    }

    private void setDecimal(double value) {
        this.kind = NodeType.DecimalLiteral;
        this.value = Double.valueOf(df.format(value));
    }

    private NumericLiteral(int value) {
        setInteger(value);
    }

    private void setInteger(int value) {
        this.kind = NodeType.IntegerLiteral;
        this.value = value;
    }

    private NumericLiteral(String value) {
        setValue(value);
    }

    private NumericLiteral(Object value) {
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

    public static Expression of(Object value) {
        return new NumericLiteral(value);
    }

    public static Expression of(int value) {
        return new NumericLiteral(value);
    }

    public static Literal of(float value) {
        return new NumericLiteral(value);
    }

    public static Literal of(double value) {
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
    public Object getVal() {
        return value;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.eval(this);
    }
}
