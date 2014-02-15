package parser.nodes.impl;

import parser.nodes.Node;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 30.12.13
 * Time: 8:44
 */
public class ArithmeticNode extends Node {

    private String expression;

    public ArithmeticNode(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public String translate() {
        return String.format("(%s)", expression);
    }
}
