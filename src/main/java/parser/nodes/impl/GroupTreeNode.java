package parser.nodes.impl;

import parser.expressions.Expression;
import parser.nodes.TreeNode;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 23.03.14
 * Time: 21:10
 */
public class GroupTreeNode extends TreeNode {
    protected Expression groupExpression;

    public GroupTreeNode(TreeNode from, List<Field> fieldNames, Expression groupExpression) {
        super(from, fieldNames, 0);
        this.groupExpression = groupExpression;
    }

    @Override
    public String translate() {
        StringBuilder res = new StringBuilder();
        String shift = shiftRight();

        res.append(String.format("\n%sfor %s in ", shift, getVar()));

        getFrom().setLevel(this.getLevel() + 1);
        res.append(getFrom().translate());
        String groupBy = TreeNode.getNextVar();
        String group = TreeNode.getNextVar();
        res.append(String.format("\n%s\tgroup by %s := %s with %s",
                shift, groupBy, groupExpression.translate(), group));
        res.append(String.format("\n%sreturn {", shift));
        res.append(String.format("\n\t%s\"%s\": %s,\n", shift, groupExpression.translate(), groupBy));
        res.append(String.format("\n\t%s\"group\": %s,\n", shift, group));
        res.append(String.format("\n\t%s\"score\": 1.0\n", shift));
        res.append(String.format("\n%s}", shift));

        return res.toString().replace("{{var1}}", getVar());
    }

    @Override
    public String toString() {
        return "GroupTreeNode{" +
                "level=" + getLevel() +
                ", var='" + getVar() + '\'' +
                ", from=" + getFrom() +
                ", fieldNames=" + getFieldNames() +
                ", groupExpression=" + groupExpression +
                '}';
    }
}
