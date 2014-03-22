package parser.nodes.impl;

import parser.expressions.Expression;
import parser.nodes.TreeNode;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 14.02.14
 * Time: 14:12
 */
public class FilterTreeNode extends TreeNode {
    private Expression where;
    private Map<String, String> fieldAliases;

    public FilterTreeNode(TreeNode from, Expression where, Map<String, String> fieldAliases) {
        super(from, new LinkedList<String>(fieldAliases.values()));
        this.where = where;
        this.fieldAliases = fieldAliases;
    }

    @Override
    public String translate() {
        StringBuilder res = new StringBuilder();
        String shift = shiftRight();

        res.append(String.format("\n%sfor %s in ", shift, getVar()));

        getFrom().setLevel(this.getLevel() + 1);
        res.append(getFrom().translate());

        res.append(String.format("\n%s\twhere %s", shift, where.translate()));

        res.append(String.format("\n%sreturn {\n\t", shift));

        for (Iterator<String> iter = fieldAliases.keySet().iterator(); iter.hasNext();) {
            String key = iter.next();
            res.append(String.format("%s\"%s\":%s.%s", shift, fieldAliases.get(key), getVar(), key));
            res.append(iter.hasNext() ? ",\n\t" : String.format("\n%s}", shift));
        }
        return res.toString().replace("{{var1}}", getVar());
    }

    @Override
    public String toString() {
        return "FilterTreeNode{" +
                "level=" + getLevel() +
                ", var='" + getVar() + '\'' +
                ", from=" + getFrom() +
                ", fieldNames=" + getFieldNames() +
                ", fieldAliases=" + fieldAliases +
                ", where=" + where +
                '}';
    }
}
