package parser.nodes;
/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 30.12.13
 * Time: 8:04
 */
public abstract class Node {
    private int level = 0;

    public String shiftRight() {
        StringBuilder translation = new StringBuilder();
        for(int i = 0; i < level; i++) {
            translation.append('\t');
        }
        return translation.toString();
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public abstract String translate();
}
