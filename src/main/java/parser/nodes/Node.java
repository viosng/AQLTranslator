package parser.nodes;

import parser.Translatable;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 30.12.13
 * Time: 8:04
 */
public class Node implements Translatable {
    private static int varNumber = 0;

    public static String getNextVar() {
        return String.format("$var%d", varNumber++);
    }

    private int level = 0;
    private String var;
    private Node from;
    private List<String> fieldNames;

    public Node(Node from, List<String> fieldNames) {
        this.var = getNextVar();
        this.from = from;
        this.fieldNames = fieldNames;
    }

    public Node(Node from) {
        this.var = getNextVar();
        this.from = from;
        this.fieldNames = from.getFieldNames();
    }

    public Node() {
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }

    public String getVar() {
        return var;
    }

    public void setFieldNames(List<String> fieldNames) {
        this.fieldNames = fieldNames;
    }

    public Node getFrom() {
        return from;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String shiftRight() {
        StringBuilder translation = new StringBuilder();
        for(int i = 0; i < level; i++) {
            translation.append('\t');
        }
        return translation.toString();
    }

    @Override
    public String translate() {
        StringBuilder res = new StringBuilder();
        String shift = shiftRight();

        res.append(String.format("\n%sfor %s in ", shift, var));

        from.setLevel(this.getLevel() + 1);
        res.append(from.translate());

        res.append(String.format("\n%sreturn %s", shift, var));
        return res.toString();
    }

    @Override
    public String toString() {
        return "Node{" +
                "level=" + level +
                ", var='" + var + '\'' +
                ", from=" + from +
                ", fieldNames=" + fieldNames +
                '}';
    }
}
