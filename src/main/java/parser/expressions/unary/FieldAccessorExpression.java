package parser.expressions.unary;

import parser.expressions.Expression;
import parser.nodes.Node;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 02.03.14
 * Time: 12:14
 */
public class FieldAccessorExpression implements Expression {
    private Node container;
    private String field;


    public FieldAccessorExpression(Node container, String field) {
        this.container = container;
        this.field = field;
    }

    @Override
    public String translate() {
        if(container.getFieldNames().contains(field)) return String.format("%s.%s", container.getVar(), field);
        // todo Exceptions!!!!
        System.out.println("No such field \'" + field + "\' in container:" + container.translate());
        return "null";
    }

    @Override
    public String toString() {
        return "FieldAccessorExpression{" +
                "container=" + container +
                ", field='" + field + '\'' +
                '}';
    }
}
