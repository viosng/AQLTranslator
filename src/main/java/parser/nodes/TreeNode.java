package parser.nodes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import parser.Translatable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 30.12.13
 * Time: 8:04
 */
public class TreeNode implements Translatable {
    private static int varNumber = 0;
    protected final static double BLOCK_ACCESS_TIME = 0.049;
    protected final static double VOLUME_TO_BLOCK = 1.0 / (1 << 20);

    public static String getNextVar() {
        return String.format("$var%d", varNumber++);
    }

    @JsonIgnoreProperties("{size}")
    public static class Field {

        private static Map<String, Integer> FIELD_SIZE_MAP;
        private final int DEFAULT_SIZE_IN_BYTES = 4;
        static{
            FIELD_SIZE_MAP = new HashMap<String, Integer>();
            FIELD_SIZE_MAP.put("string", 1000);
            FIELD_SIZE_MAP.put("int8", 1);
            FIELD_SIZE_MAP.put("int16", 2);
            FIELD_SIZE_MAP.put("int32", 4);
            FIELD_SIZE_MAP.put("int64", 8);
            FIELD_SIZE_MAP.put("float", 4);
            FIELD_SIZE_MAP.put("double", 8);
        }

        public Field(String name) {
            this.name = name;
        }

        private int getFieldSizeInBytes(String type) {
            Integer size = FIELD_SIZE_MAP.get(type.toLowerCase());
            return size == null ? DEFAULT_SIZE_IN_BYTES : size;
        }

        private String name, type;
        private int size = DEFAULT_SIZE_IN_BYTES;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
            size = getFieldSizeInBytes(type);
        }

        public int getSize() {
            return size;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private int level = 0;
    private String var;
    private TreeNode from;
    private List<Field> fieldNames;
    private long volume = 0, size = 0;

    private void fieldSetter(TreeNode from, List<Field> fieldNames, long size, long volume) {
        this.var = getNextVar();
        this.from = from;
        this.fieldNames = fieldNames;
        this.size = size;
        if(volume == 0) {
            for (Field field: fieldNames) {
                this.volume += field.getSize();
            }
            this.volume =  this.volume * size;
        } else {
            this.volume = volume;
        }
    }

    public TreeNode(TreeNode from, List<Field> fieldNames, long size) {
        fieldSetter(from, fieldNames, size, 0);
    }

    public TreeNode(TreeNode from, List<Field> fieldNames) {
        fieldSetter(from, fieldNames, from.getSize(), 0);
    }

    public TreeNode(TreeNode from) {
        fieldSetter(from, from.getFieldNames(), from.getSize(), from.getVolume());
    }

    public TreeNode(List<Field> fieldNames, long size) {
        fieldSetter(null, fieldNames, size, 0);
    }

    public List<Field> getFieldNames() {
        return fieldNames;
    }

    public String getVar() {
        return var;
    }

    public void setFieldNames(List<Field> fieldNames) {
        this.fieldNames = fieldNames;
    }

    public TreeNode getFrom() {
        return from;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getVolume() {
        return volume;
    }

    public long getSize() {
        return size;
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
    public double getExecutionTime() {
        return from.getExecutionTime();
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "level=" + level +
                ", var='" + var + '\'' +
                ", from=" + from +
                ", fieldNames=" + fieldNames +
                '}';
    }
}
