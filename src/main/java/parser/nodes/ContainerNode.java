package parser.nodes;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 14.02.14
 * Time: 14:02
 */
public abstract class ContainerNode extends Node {
    protected List<String> fieldNames;

    public List<String> getFieldNames() {
        return fieldNames;
    }
}
