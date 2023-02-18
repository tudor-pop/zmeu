package dev.fangscl.Runtime.TypeSystem.Expressions;

import dev.fangscl.Runtime.TypeSystem.Base.Expression;

public class ErrorExpression extends Expression {
    private String msg;
    private int position;

    public ErrorExpression(String msg) {
        this.msg = "Unknown token found during parsing: %s  ".formatted(msg);
    }

    public ErrorExpression(int position) {
        this();
        this.position = position;
        this.msg = this.msg + position;
    }

    public ErrorExpression() {
        this.msg = "Unknown token found during parsing";
    }
}
