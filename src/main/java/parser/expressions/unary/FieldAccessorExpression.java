package parser.expressions.unary;

import parser.expressions.Expression;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 02.03.14
 * Time: 12:14
 */
public class FieldAccessorExpression implements Expression {
    private String field;


    public FieldAccessorExpression(String field) {
        this.field = field;
    }

    @Override
    public String translate() {
        return String.format("{{var}}.%s", field);
    }

    @Override
    public String toString() {
        return "FieldAccessorExpression{" +
                ", field=" + field +
                '}';
    }
}
