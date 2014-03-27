package parser.nodes.impl;

import parser.expressions.Difficulty;
import parser.expressions.Expression;
import parser.nodes.TreeNode;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 03.03.14
 * Time: 9:22
 */
public class JoinTreeNode extends TreeNode {
    private TreeNode nestedFor;
    protected Expression where;
    private double time;
    private double logTime = 0;

    private final static double HASH_JOIN_TIME = 3.97e-6;
    private final static double MERGE_JOIN_TIME = 1.69e-4;
    private final static double FULL_JOIN_TIME = 3.97e-6;

    private static EnumMap<Difficulty, Double> JOIN_TIMES_MAP;
    static {
        JOIN_TIMES_MAP.put(Difficulty.HASH, HASH_JOIN_TIME);
        JOIN_TIMES_MAP.put(Difficulty.MERGE, MERGE_JOIN_TIME);
        JOIN_TIMES_MAP.put(Difficulty.FULL, FULL_JOIN_TIME);
    }


    private static List<Field> mergeLists(List<Field> first, List<Field> second) {
        List<Field> fields = new ArrayList<Field>(first);
        fields.addAll(second);
        return fields;
    }

    public JoinTreeNode(TreeNode from, TreeNode nestedFor, Expression where) {
        super(from, mergeLists(from.getFieldNames(), nestedFor.getFieldNames()), from.getSize() * nestedFor.getSize());
        this.nestedFor = new TreeNode(nestedFor);
        this.where = where;
        this.time = from.getExecutionTime() + nestedFor.getExecutionTime();
        this.time += 2 * BLOCK_ACCESS_TIME * Math.ceil((from.getVolume() + nestedFor.getVolume() * VOLUME_TO_BLOCK));
        this.logTime = from.getSize() * Math.log(from.getSize()) / Math.log(2) +
                nestedFor.getSize() * Math.log(nestedFor.getSize()) / Math.log(2);

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
        res.append(String.format("\n%s\tfor %s in ", shift, nestedFor.getVar()));
        nestedFor.getFrom().setLevel(this.getLevel() + 2);
        res.append(nestedFor.getFrom().translate());

        // make where statement
        res.append(String.format("\n%s\twhere %s", shift, where.translate()));

        // make return statement with combining field lists of two arguments
        res.append(String.format("\n%sreturn {\n\t", shift));

        for (Field field : getFrom().getFieldNames()) {
            res.append(String.format("%s\"%s\":%s.%s,\n\t", shift, field, getVar(), field));
        }

        for (Iterator<Field> iter = nestedFor.getFieldNames().iterator(); iter.hasNext();) {
            Field field = iter.next();
            res.append(String.format("%s\"%s\":%s.%s", shift, field, nestedFor.getVar(), field));
            res.append(iter.hasNext() ? ",\n\t" : String.format("\n%s}", shift));
        }
        return res.toString().replace("{{var1}}", getVar()).replace("{{var2}}", nestedFor.getVar());
    }

    @Override
    public String toString() {
        return "JoinTreeNode{" +
                "level=" + getLevel() +
                ", var='" + getVar() + '\'' +
                ", from=" + getFrom() +
                ", fieldNames=" + getFieldNames() +
                ", nestedFor=" + nestedFor +
                ", where=" + where +
                '}';
    }

    @Override
    public double getExecutionTime() {
        return this.time + this.logTime * JOIN_TIMES_MAP.get(where.getDifficulty());
    }
}
