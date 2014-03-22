package parser;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import parser.expressions.Expression;
import parser.expressions.binary.impl.LessExpression;
import parser.expressions.unary.ConstantExpression;
import parser.expressions.unary.FieldAccessorExpression;
import parser.nodes.Node;
import parser.nodes.impl.DatasetNode;
import parser.nodes.impl.FilterNode;
import parser.nodes.impl.JoinNode;

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

    private Translatable root;

    public AQLSyntaxTree(Element root) {
        this.root = parse(root);
    }

    private static interface NodeBuilder {
        public Node build(Element root);
    }

    private static interface ExpressionBuilder {
        public Expression build(Element root);
    }

    public static Element getElementByName(Element root, String name, int i) {
        return (Element)root.getElementsByTagName(name).item(i);
    }

    public static Element getElementByName(Element root, String name) {
        return getElementByName(root, name, 0);
    }

    public static String getTextByName(Element root, String name) {
        return getElementByName(root, name).getTextContent();
    }

    private static final Map<String, NodeBuilder> NODE_BUILDER_MAP;
    static {
        NODE_BUILDER_MAP = new HashMap<String, NodeBuilder>();

        NODE_BUILDER_MAP.put("filter", new NodeBuilder() {
            @Override
            public Node build(Element root) {
                Element base = getElementByName(root, "query");
                Element expr = getElementByName(root, "expression");

                Map<String, String> fieldAliases = new HashMap<String, String>();
                NodeList fields = getElementByName(root, "select").getElementsByTagName("field");
                for (int i = 0; i < fields.getLength(); i++) {
                    Element field = (Element) fields.item(i);
                    fieldAliases.put(getTextByName(field, "name"), getTextByName(field, "alias"));
                }
                return new FilterNode(
                        NODE_BUILDER_MAP.get(getTextByName(base, "name")).build(base),
                        EXPRESSION_BUILDER_MAP.get(getTextByName(expr, "name")).build(expr),
                        fieldAliases
                );
            }
        });

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
                Element expr = getElementByName(root, "expression");
                return new JoinNode(
                        NODE_BUILDER_MAP.get(getTextByName(arg1, "name")).build(arg1),
                        NODE_BUILDER_MAP.get(getTextByName(arg2, "name")).build(arg2),
                        EXPRESSION_BUILDER_MAP.get(getTextByName(expr, "name")).build(expr)
                );
            }
        });
    }

    private static final Map<String, ExpressionBuilder> EXPRESSION_BUILDER_MAP;
    static {
        EXPRESSION_BUILDER_MAP = new HashMap<String, ExpressionBuilder>();
        EXPRESSION_BUILDER_MAP.put("less", new ExpressionBuilder() {
            @Override
            public Expression build(Element root) {
                NodeList queries = root.getElementsByTagName("expression");
                Element arg1 = (Element) queries.item(0);
                Element arg2 = (Element) queries.item(1);
                return new LessExpression(
                        EXPRESSION_BUILDER_MAP.get(getTextByName(arg1, "name")).build(arg1),
                        EXPRESSION_BUILDER_MAP.get(getTextByName(arg2, "name")).build(arg2)
                );
            }
        });
        EXPRESSION_BUILDER_MAP.put("constant", new ExpressionBuilder() {
            @Override
            public Expression build(Element root) {
                return new ConstantExpression(getTextByName(root, "arg"));
            }
        });
        EXPRESSION_BUILDER_MAP.put("field_access", new ExpressionBuilder() {
            @Override
            public Expression build(Element root) {
                return new FieldAccessorExpression(getTextByName(root, "arg"), getTextByName(root, "scope"));
            }
        });

    }

    public static Node parse(Element root) {
        return NODE_BUILDER_MAP.get(getTextByName(root, "name")).build(root);
    }


    public String translate() {
        return root.translate();
    }
}
