package parser.expressions.unary.impl;

import parser.expressions.Difficulty;
import parser.expressions.unary.UnaryExpression;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 03.03.14
 * Time: 9:06
 */
public class ConstantExpression implements UnaryExpression {
    private String constant;

    public ConstantExpression(String constant) {
        this.constant = constant;
    }

    @Override
    public String translate() {
        return constant;
    }

    @Override
    public double getExecutionTime() {
        return 0;
    }

    @Override
    public String toString() {
        return "ConstantExpression{" +
                "constant=" + constant +
                '}';
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.HASH;
    }
}
