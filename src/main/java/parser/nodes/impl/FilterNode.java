package parser.nodes.impl;

import parser.expressions.binary.BinaryExpression;
import parser.nodes.Node;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 14.02.14
 * Time: 14:12
 */
public class FilterNode extends Node {
    private BinaryExpression where;

    public FilterNode(Node from, BinaryExpression where) {
        super(from);
        this.where = where;
    }

    @Override
    public String translate() {
        StringBuilder res = new StringBuilder();
        String shift = shiftRight();

        res.append(String.format("\n%sfor %s in ", shift, getVar()));

        getFrom().setLevel(this.getLevel() + 1);
        res.append(getFrom().translate());

        res.append(String.format("\n%s\twhere %s", shift, where.translate()));

        res.append(String.format("\n%sreturn %s", shift, getVar()));
        return res.toString();
    }

    @Override
    public String toString() {
        return "FilterNode{" +
                "level=" + getLevel() +
                ", var='" + getVar() + '\'' +
                ", from=" + getFrom() +
                ", fieldNames=" + getFieldNames() +
                ", where=" + where +
                '}';
    }
}
