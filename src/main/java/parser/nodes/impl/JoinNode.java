package parser.nodes.impl;

import parser.expressions.Expression;
import parser.nodes.Node;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 03.03.14
 * Time: 9:22
 */
public class JoinNode extends Node {
    private Node nestedFor;
    protected Expression where;

    private static List<String> mergeLists(List<String> first, List<String> second) {
        List<String> fields = new LinkedList<String>(first);
        fields.addAll(second);
        return fields;
    }

    public JoinNode(Node from, Node nestedFor, Expression where) {
        super(from, mergeLists(from.getFieldNames(), nestedFor.getFieldNames()));
        this.nestedFor = new Node(nestedFor);
        this.where = where;
    }

    @Override
    public String translate() {
        StringBuilder res = new StringBuilder();
        String shift = shiftRight();

        // make first loop
        res.append(String.format("\n%sfor %s in ", shift, getVar()));
        getFrom().setLevel(this.getLevel() + 1);
        res.append(getFrom().translate());

        // make second loop
        //res.append(String.format("\n%s\tfor %s in ", shift, nestedFor.getVar()));
        nestedFor.setLevel(this.getLevel() + 1);
        res.append(nestedFor.translate());

        // make where statement
        res.append(String.format("\n%s\twhere %s", shift, where.translate()));

        // make return statement with combining field lists of two arguments
        res.append(String.format("\n%sreturn {\n\t", shift));

        for (String field : getFrom().getFieldNames()) {
            res.append(String.format("%s\"%s\":%s.%s,\n\t", shift, field, getVar(), field));
        }

        for (Iterator<String> iter = nestedFor.getFieldNames().iterator(); iter.hasNext();) {
            String field = iter.next();
            res.append(String.format("%s\"%s\":%s.%s", shift, field, nestedFor.getVar(), field));
            res.append(iter.hasNext() ? ",\n\t" : String.format("\n%s}", shift));
        }
        return res.toString();
    }

    @Override
    public String toString() {
        return "JoinNode{" +
                "level=" + getLevel() +
                ", var='" + getVar() + '\'' +
                ", from=" + getFrom() +
                ", fieldNames=" + getFieldNames() +
                ", nestedFor=" + nestedFor +
                ", where=" + where +
                '}';
    }
}
