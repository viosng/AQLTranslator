    package parser.nodes.impl;

import parser.nodes.ContainerNode;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 09.02.14
 * Time: 13:45
 */
public class JoinNode extends ContainerNode {
    protected String leftVar, rightVar;
    protected ContainerNode left, right;
    protected ArithmeticNode where;

    public JoinNode(String leftVar, String rightVar, ContainerNode left, ContainerNode right, ArithmeticNode where) {
        this.leftVar = leftVar;
        this.rightVar = rightVar;
        this.left = left;
        this.right = right;
        this.where = where;
        this.fieldNames = new LinkedList<String>(left.getFieldNames());
        this.fieldNames.addAll(right.getFieldNames());
    }

    @Override
    public String translate() {
        StringBuilder res = new StringBuilder();
        String shift = shiftRight();

        // make first loop
        res.append(String.format("\n%sfor %s in ", shift, leftVar));
        left.setLevel(this.getLevel() + 1);
        res.append(left.translate());

        // make second loop
        res.append(String.format("\n%s\tfor %s in ", shift, rightVar));
        right.setLevel(this.getLevel() + 2);
        res.append(right.translate());

        // make where statement
        res.append(String.format("\n%s\twhere %s", shift, where.translate()));

        // make return statement with combining field lists of two arguments
        res.append(String.format("\n%sreturn {\n\t", shift));
        for (Iterator<String> iter = left.getFieldNames().iterator(); iter.hasNext();) {
            String field = iter.next();
            res.append(String.format("%s\"%s\":%s.%s,\n\t", shift, field, leftVar, field));
        }

        for (Iterator<String> iter = right.getFieldNames().iterator(); iter.hasNext();) {
            String field = iter.next();
            res.append(String.format("%s\"%s\":%s.%s", shift, field, rightVar, field));
            res.append(iter.hasNext() ? ",\n\t" : String.format("\n%s}", shift));
        }
        return res.toString();
    }
}
