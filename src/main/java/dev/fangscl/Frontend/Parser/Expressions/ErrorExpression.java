package dev.fangscl.Frontend.Parser.Expressions;

public class ErrorExpression extends Expression {
    private String msg;
    private int position;

    public ErrorExpression(String msg) {
        setMsg(msg);
    }

    public ErrorExpression(Object msg) {
        if (msg instanceof String s) {
            setMsg(s);
        }
    }

    private void setMsg(String msg) {
        String template = "Unknown token found during parsing: %s  ";
        this.msg = template.formatted(msg);
    }

    public ErrorExpression(int position) {
        this();
        this.position = position;
        this.msg = this.msg + position;
    }

    public ErrorExpression() {
        this.msg = "Unknown token found during parsing";
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }

}
