package parser.nodes.impl;

import parser.expressions.Expression;
import parser.nodes.TreeNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    private static List<Field> getNewFields(TreeNode from, Map<String, String> fieldAliases) {
        if(fieldAliases.size() == 0) return from.getFieldNames();
        List<Field> newFields = new ArrayList<Field>();
        for(Field field : from.getFieldNames()) {
            for(String alias : fieldAliases.keySet()) {
                if (field.getName().equals(alias)) {
                    field.setName(fieldAliases.get(alias));
                    newFields.add(field);
                    break;
                }
            }
        }
        return newFields;
    }

    public FilterTreeNode(TreeNode from, Expression where, Map<String, String> fieldAliases) {
        super(from, getNewFields(from, fieldAliases));
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

        res.append(String.format("\n%sreturn %s", shift, (fieldAliases.size() > 0 ? "{\n\t" : getVar())));

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
