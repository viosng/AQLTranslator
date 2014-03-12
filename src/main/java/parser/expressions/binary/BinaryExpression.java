package parser.expressions.binary;

import parser.expressions.Expression;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 03.03.14
 * Time: 8:32
 */
public abstract class BinaryExpression implements Expression {
    private Expression left, right;

    protected BinaryExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "BinaryExpression{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
