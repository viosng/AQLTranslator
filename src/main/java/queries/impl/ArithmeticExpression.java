package queries.impl;

import queries.Expression;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 30.12.13
 * Time: 8:44
 */
public class ArithmeticExpression implements Expression {

    private String expression;
    private boolean hasVariableReference;

    public ArithmeticExpression(String expression) {
        this.expression = expression;
        this.hasVariableReference = expression.contains("$this");
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
        this.hasVariableReference = expression.contains("$this");
    }

    @Override
    public String translate(String var) {
        return String.format("(%s)", expression.replaceAll("\\$this", "\\"+ var));
    }

    @Override
    public String translate() throws IllegalStateException{
        if(hasVariableReference)
            throw new IllegalStateException("Arithmetic exception contains variable reference.");
        else
            return String.format("(%s)", expression);
    }
}
