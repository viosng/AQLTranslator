package parser.nodes.impl;

import parser.nodes.ContainerNode;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 12.02.14
 * Time: 16:50
 */
public class ProjectionNode extends ContainerNode {
    private String var;
    private ContainerNode from;
    private Map<String, String> fieldAliases;

    public ProjectionNode(String var, ContainerNode from, Map<String, String> fieldAliases) {
        this.var = var;
        this.from = from;
        this.fieldAliases = fieldAliases;
        this.fieldNames = new LinkedList<String>(fieldAliases.values());
    }

    @Override
    public String translate() {
        StringBuilder res = new StringBuilder();
        String shift = shiftRight();

        res.append(String.format("\n%sfor %s in ", shift, var));

        from.setLevel(this.getLevel() + 1);
        res.append(from.translate());

        res.append(String.format("\n%sreturn {\n\t", shift));

        for (Iterator<String> iter = fieldAliases.keySet().iterator(); iter.hasNext();) {
            String key = iter.next();
            res.append(String.format("%s\"%s\":%s.%s", shift, fieldAliases.get(key), var, key));
            res.append(iter.hasNext() ? ",\n\t" : String.format("\n%s}", shift));
        }
        return res.toString();
    }
}
