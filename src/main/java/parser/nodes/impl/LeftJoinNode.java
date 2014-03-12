package parser.nodes.impl;

import parser.expressions.ArithmeticNode;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 14.02.14
 * Time: 22:00
 */
public class LeftJoinNode extends JoinNode {

    public LeftJoinNode(String leftVar, String rightVar, ContainerNode left, ContainerNode right, ArithmeticNode where) {
        super(left, right, where);
    }

    @Override
    public String translate() {
        /*StringBuilder res = new StringBuilder();
        String shift = shiftRight();

        // make first loop
        res.append(String.format("\n%sfor %s in ", shift, leftVar));
        left.setLevel(this.getLevel() + 1);
        res.append(left.translate());
        res.append(String.format("\n%sreturn %s", shift, leftVar));

        // make second loop
        res.append(String.format("\n%s\tfor %s in ", shift, rightVar));
        right.setLevel(this.getLevel() + 2);
        res.append(right.translate());
        res.append(String.format("\n%s\treturn %s", shift, rightVar));

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
        return res.toString();*/
        return null;
    }
}
