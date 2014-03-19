package parser.expressions.binary.impl;

import parser.expressions.Expression;
import parser.expressions.binary.BinaryExpression;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 03.03.14
 * Time: 8:38
 */
public class LessExpression extends BinaryExpression {
    public LessExpression(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String translate() {
        return String.format("%s < %s", getLeft().translate(), getRight().translate());
    }

    @Override
    public String toString() {
        return "LessExpression{" +
                "left=" + getLeft() +
                ", right=" + getRight() +
                '}';
    }
}
