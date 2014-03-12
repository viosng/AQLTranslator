package parser.expressions.unary;

import parser.expressions.Expression;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 03.03.14
 * Time: 9:06
 */
public class ConstantExpression implements Expression {
    private String constant;

    @Override
    public String translate() {
        return constant;
    }

    @Override
    public String toString() {
        return "ConstantExpression{" +
                "constant='" + constant + '\'' +
                '}';
    }
}
