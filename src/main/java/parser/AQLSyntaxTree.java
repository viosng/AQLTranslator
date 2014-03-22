package parser;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import parser.expressions.Expression;
import parser.expressions.binary.impl.EqualityExpression;
import parser.expressions.binary.impl.LessExpression;
import parser.expressions.unary.ConstantExpression;
import parser.expressions.unary.FieldAccessorExpression;
import parser.nodes.TreeNode;
import parser.nodes.impl.DatasetTreeNode;
import parser.nodes.impl.FilterTreeNode;
import parser.nodes.impl.JoinTreeNode;

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

    public static class MyXMLElement {
        private Element element;

        public MyXMLElement(Element element) {
            this.element = element;
        }

        private Element getElement() {
            return element;
        }

        private String getText() {
            return element.getTextContent();
        }

        private String getOperationName() {
            return this.getFirstChildElement().getFirstChildElement().getText();
        }

        private String getExpressionName() {
            return this.getFirstChildElement().getText();
        }

        public MyXMLElement getFirstChildElement() {
            NodeList children = element.getChildNodes();
            for(int i = 0; i < children.getLength(); i++) {
                Node c = children.item(i);
                if(c instanceof Element) return new MyXMLElement((Element)c);
            }
            return null;
        }

        public MyXMLElement getNextSiblingElement() {
            Node next = element.getNextSibling();
            while(next != null && !(next instanceof Element)) next = next.getNextSibling();
            return new MyXMLElement((Element)next);
        }
    }

    private Translatable root;

    public AQLSyntaxTree(MyXMLElement root) {
        this.root = parse(root);
    }

    private static interface NodeBuilder {
        public TreeNode build(MyXMLElement root);
    }

    private static interface ExpressionBuilder {
        public Expression build(MyXMLElement root);
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
            public TreeNode build(MyXMLElement root) {
                MyXMLElement operation = root.getFirstChildElement();
                MyXMLElement base = operation.getNextSiblingElement().getFirstChildElement();
                MyXMLElement select = operation.getFirstChildElement().getNextSiblingElement().getFirstChildElement();
                MyXMLElement expr = select.getNextSiblingElement();
                Map<String, String> fieldAliases = new HashMap<String, String>();
                NodeList fields = select.getElement().getElementsByTagName("field");
                for (int i = 0; i < fields.getLength(); i++) {
                    Element field = (Element) fields.item(i);
                    fieldAliases.put(getTextByName(field, "name"), getTextByName(field, "alias"));
                }
                return new FilterTreeNode(
                        NODE_BUILDER_MAP.get(base.getOperationName()).build(base),
                        EXPRESSION_BUILDER_MAP.get(expr.getExpressionName()).build(expr),
                        fieldAliases
                );
            }
        });

        NODE_BUILDER_MAP.put("data", new NodeBuilder() {
            @Override
            public TreeNode build(MyXMLElement root) {
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
                    return new DatasetTreeNode(getTextByName(root, "source"), Arrays.asList("name", "age","id", "department_id"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;*/
                return new DatasetTreeNode(root
                        .getFirstChildElement()
                        .getFirstChildElement()
                        .getNextSiblingElement()
                        .getFirstChildElement()
                        .getText(), Arrays.asList("name", "age","id", "department_id"));
            }
        });

        NODE_BUILDER_MAP.put("join", new NodeBuilder() {
            @Override
            public TreeNode build(MyXMLElement root) {
                MyXMLElement operation = root.getFirstChildElement();
                MyXMLElement arg1 = operation.getNextSiblingElement().getFirstChildElement();
                MyXMLElement arg2 = arg1.getNextSiblingElement();
                MyXMLElement expr = operation.getFirstChildElement().getNextSiblingElement().getFirstChildElement();
                return new JoinTreeNode(
                        NODE_BUILDER_MAP.get(arg1.getOperationName()).build(arg1),
                        NODE_BUILDER_MAP.get(arg2.getOperationName()).build(arg2),
                        EXPRESSION_BUILDER_MAP.get(expr.getFirstChildElement().getText()).build(expr)
                );
            }
        });
    }

    private static final Map<String, ExpressionBuilder> EXPRESSION_BUILDER_MAP;
    static {
        EXPRESSION_BUILDER_MAP = new HashMap<String, ExpressionBuilder>();
        EXPRESSION_BUILDER_MAP.put("less", new ExpressionBuilder() {
            @Override
            public Expression build(MyXMLElement root) {
                MyXMLElement arg1 = root.getFirstChildElement().getNextSiblingElement();
                MyXMLElement arg2 = arg1.getNextSiblingElement();
                return new LessExpression(
                        EXPRESSION_BUILDER_MAP.get(arg1.getExpressionName()).build(arg1),
                        EXPRESSION_BUILDER_MAP.get(arg2.getExpressionName()).build(arg2)
                );
            }
        });
        EXPRESSION_BUILDER_MAP.put("equal", new ExpressionBuilder() {
            @Override
            public Expression build(MyXMLElement root) {
                MyXMLElement arg1 = root.getFirstChildElement().getNextSiblingElement();
                MyXMLElement arg2 = arg1.getNextSiblingElement();
                return new EqualityExpression(
                        EXPRESSION_BUILDER_MAP.get(arg1.getExpressionName()).build(arg1),
                        EXPRESSION_BUILDER_MAP.get(arg2.getExpressionName()).build(arg2)
                );
            }
        });
        EXPRESSION_BUILDER_MAP.put("constant", new ExpressionBuilder() {
            @Override
            public Expression build(MyXMLElement root) {
                return new ConstantExpression(root.getFirstChildElement().getNextSiblingElement().getText());
            }
        });
        EXPRESSION_BUILDER_MAP.put("field_access", new ExpressionBuilder() {
            @Override
            public Expression build(MyXMLElement root) {
                MyXMLElement field = root.getFirstChildElement().getNextSiblingElement();
                return new FieldAccessorExpression(field.getText(), field.getNextSiblingElement().getText());
            }
        });

    }

    public static TreeNode parse(MyXMLElement root) {
        return NODE_BUILDER_MAP.get(root.getOperationName()).build(root);
    }

    public static void print(Element root) {
        Element next = root;
        Node node;
        while(next != null) {
            System.out.println(next);
            node = next.getNextSibling();
            while(node != null && !(node instanceof Element)) node = node.getNextSibling();
            next = (Element)node;
        }
    }

    public String translate() {
        return root.translate();
    }
}
