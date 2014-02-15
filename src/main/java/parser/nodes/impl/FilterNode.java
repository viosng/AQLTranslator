package parser.nodes.impl;

import parser.nodes.ContainerNode;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 14.02.14
 * Time: 14:12
 */
public class FilterNode extends ContainerNode {
    private String var;
    private ContainerNode from;
    private ArithmeticNode where;

    public FilterNode(String var, ContainerNode from, ArithmeticNode where) {
        this.var = var;
        this.from = from;
        this.where = where;
        this.fieldNames = from.getFieldNames();
    }

    @Override
    public String translate() {
        StringBuilder res = new StringBuilder();
        String shift = shiftRight();

        res.append(String.format("\n%sfor %s in ", shift, var));

        from.setLevel(this.getLevel() + 1);
        res.append(from.translate());

        res.append(String.format("\n%s\twhere %s", shift, where.translate()));

        res.append(String.format("\n%sreturn %s", shift, var));
        return res.toString();
    }
}
