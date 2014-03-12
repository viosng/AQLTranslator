package parser;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import parser.nodes.impl.ContainerNode;
import parser.nodes.Node;
import parser.expressions.ArithmeticNode;
import parser.nodes.impl.DatasetNode;
import parser.nodes.impl.FilterNode;
import parser.nodes.impl.JoinNode;
import parser.nodes.impl.ProjectionNode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 14.02.14
 * Time: 13:49
 */
public class AQLSyntaxTree {

    private Node root;

    public AQLSyntaxTree(Element root) {
        this.root = parse(root);
    }

    private static interface NodeBuilder {
        public Node build(Element root);
    }

    public static Element getElementByName(Element root, String name) {
        return (Element)root.getElementsByTagName(name).item(0);
    }

    public static String getTextByName(Element root, String name) {
        return getElementByName(root, name).getTextContent();
    }

    //todo add xml library, see guava
    private static final Map<String, NodeBuilder> NODE_BUILDER_MAP;
    static {
        NODE_BUILDER_MAP = new HashMap<String, NodeBuilder>();

        NODE_BUILDER_MAP.put("filter", new NodeBuilder() {
            @Override
            public Node build(Element root) {
                Element base = getElementByName(root, "query");
                return new FilterNode(
                        NODE_BUILDER_MAP.get(getTextByName(base, "name")).build(base),
                        new ArithmeticNode(getTextByName(root, "expression"))
                );
            }
        });

        // todo make better code, add json parser library
        NODE_BUILDER_MAP.put("data", new NodeBuilder() {
            @Override
            public Node build(Element root) {
                /*String source = root.getElementsByTagName("source").item(0).getTextContent();
                String query = String.format("for $a in(\n" +
                        "    for $r in dataset Metadata.Datatype\n" +
                        "        for $s in dataset Metadata.Dataset\n" +
                        "            where $s.DatasetName=\"%s\" and $r.DatatypeName =  $s.DataTypeName\n" +
                        "    return $r.Derived.Record.Fields)\n" +
                        "    for $b in $a\n" +
                        "return $b.FieldName;", source);
                System.out.println(query);
                try {
                    String response = Main.getResponse(query).get(0);
                    List<String> names = Arrays.asList(response.substring(response.indexOf(':') + 2, response.length() - 2)
                            .replaceAll("\\\\n", "").replaceAll("\"", "").replaceAll("\\\\", "").split(","));
                    return new DatasetNode(getTextByName(root, "source"), Arrays.asList("name", "age","id", "department_id"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;*/
                return new DatasetNode(getTextByName(root, "source"), Arrays.asList("name", "age","id", "department_id"));
            }
        });

        NODE_BUILDER_MAP.put("join", new NodeBuilder() {
            @Override
            public Node build(Element root) {
                Element args = getElementByName(root, "arguments");
                NodeList queries = args.getElementsByTagName("query");
                Element arg1 = (Element) queries.item(0);
                Element arg2 = (Element) queries.item(1);
                return new JoinNode(
                        NODE_BUILDER_MAP.get(getTextByName(arg1, "name")).build(arg1),
                        NODE_BUILDER_MAP.get(getTextByName(arg2, "name")).build(arg2),
                        new ArithmeticNode(getTextByName(root, "expression"))
                );
            }
        });

        NODE_BUILDER_MAP.put("project", new NodeBuilder() {
            @Override
            public Node build(Element root) {
                Element base = (Element) root.getElementsByTagName("query").item(0);
                Map<String, String> fieldAliases = new HashMap<String, String>();
                NodeList fields = getElementByName(root, "select").getElementsByTagName("field");
                for (int i = 0; i < fields.getLength(); i++) {
                    Element field = (Element) fields.item(i);
                    fieldAliases.put(field.getElementsByTagName("name").item(0).getTextContent(),
                            field.getElementsByTagName("alias").item(0).getTextContent());
                }
                return new ProjectionNode(
                        NODE_BUILDER_MAP.get(getTextByName(base, "name")).build(base),
                        fieldAliases
                );
            }
        });
    }

    public static Node parse(Element root) {
        return NODE_BUILDER_MAP.get(root.getElementsByTagName("name").item(0).getTextContent()).build(root);
    }


    public String translate() {
        this.root.setLevel(0);
        return root.translate();
    }
}
