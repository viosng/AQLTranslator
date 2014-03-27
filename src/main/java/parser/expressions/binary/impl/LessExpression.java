package parser.expressions.binary.impl;

import parser.expressions.Difficulty;
import parser.expressions.Expression;
import parser.expressions.binary.BinaryExpression;
import parser.expressions.unary.UnaryExpression;

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
    public double getExecutionTime() {
        return 0;
    }

    @Override
    public String toString() {
        return "LessExpression{" +
                "left=" + getLeft() +
                ", right=" + getRight() +
                '}';
    }

    @Override
    public Difficulty getDifficulty() {
        if(getLeft() instanceof UnaryExpression && getRight() instanceof UnaryExpression) {
            return Difficulty.MERGE;
        } else {
            return Difficulty.FULL;
        }
    }
}
