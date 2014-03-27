package parser.expressions.unary.impl;

import parser.expressions.Difficulty;
import parser.expressions.unary.UnaryExpression;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 02.03.14
 * Time: 12:14
 */
public class FieldAccessorExpression implements UnaryExpression {
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
    public double getExecutionTime() {
        return 0;
    }

    @Override
    public String toString() {
        return "FieldAccessorExpression{" +
                "field='" + field + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.HASH;
    }
}
