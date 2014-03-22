package parser.expressions.unary;

import parser.expressions.Expression;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 02.03.14
 * Time: 12:14
 */
public class FieldAccessorExpression implements Expression {
    private String field, scope;


    public FieldAccessorExpression(String field, String scope) {
        this.field = field;
        this.scope = scope;
    }

    @Override
    public String translate() {
        return String.format("{{var%s}}.%s", scope, field);
    }

    @Override
    public String toString() {
        return "FieldAccessorExpression{" +
                "field='" + field + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}
