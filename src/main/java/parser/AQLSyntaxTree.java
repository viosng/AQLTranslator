package parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import parser.expressions.Expression;
import parser.expressions.binary.impl.EqualityExpression;
import parser.expressions.binary.impl.LessExpression;
import parser.expressions.unary.impl.ConstantExpression;
import parser.expressions.unary.impl.FieldAccessorExpression;
import parser.nodes.TreeNode;
import parser.nodes.impl.DatasetTreeNode;
import parser.nodes.impl.FilterTreeNode;
import parser.nodes.impl.JoinTreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: saveln
 * Date: 14.02.14
 * Time: 13:49
 */
public class AQLSyntaxTree implements Translatable{

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

    public static class ArrayWrapper {
        private List<TreeNode.Field> results;

        public ArrayWrapper(List<TreeNode.Field> results) {
            this.results = results;
        }

        public List<TreeNode.Field> getResults() {
            return results;
        }

        public void setResults(List<TreeNode.Field> results) {
            this.results = results;
        }
    }

    private Translatable root;
    private AsterixConnector connector;

    public AQLSyntaxTree(AsterixConnector connector, MyXMLElement root) {
        this.connector = connector;
        this.root = parse(connector, root);
    }

    private static interface NodeBuilder {
        public TreeNode build(AsterixConnector connector, MyXMLElement root);
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
            public TreeNode build(AsterixConnector connector, MyXMLElement root) {
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
                        NODE_BUILDER_MAP.get(base.getOperationName()).build(connector, base),
                        EXPRESSION_BUILDER_MAP.get(expr.getExpressionName()).build(expr),
                        fieldAliases
                );
            }
        });

        NODE_BUILDER_MAP.put("data", new NodeBuilder() {
            @Override
            public TreeNode build(AsterixConnector connector, MyXMLElement root) {
                String source = root
                        .getFirstChildElement()
                        .getFirstChildElement()
                        .getNextSiblingElement()
                        .getFirstChildElement()
                        .getText();
                ObjectMapper mapper = new ObjectMapper();
                String query = String.format("use dataverse Test;for $a in(for $r in dataset Metadata.Datatype\n" +
                        " for $s in dataset Metadata.Dataset\n" +
                        " where $s.DatasetName=\"%s\" and $r.DatatypeName =  $s.DataTypeName\n" +
                        "return $r.Derived.Record.Fields)\n" +
                        "for $b in $a\n" +
                        "return $b;", source);
                List<TreeNode.Field> fields = new ArrayList<TreeNode.Field>();
                int size = 0;
                try {
                    String response = connector.getResponse(query).replace("FieldName", "name").replace("FieldType", "type");
                    response = response.substring(response.indexOf('['), response.lastIndexOf(']') + 1);
                    fields = mapper.readValue(response, new TypeReference<List<TreeNode.Field>>(){});
                    response = connector.getResponse(String.format(
                            "use dataverse Test;count(for $fbu in dataset %s return $fbu);", source));
                    response = response.substring(response.indexOf('[') + 2, response.indexOf('i'));
                    size = Integer.parseInt(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new DatasetTreeNode(source, fields, size);
            }
        });

        NODE_BUILDER_MAP.put("join", new NodeBuilder() {
            @Override
            public TreeNode build(AsterixConnector connector, MyXMLElement root) {
                MyXMLElement operation = root.getFirstChildElement();
                MyXMLElement arg1 = operation.getNextSiblingElement().getFirstChildElement();
                MyXMLElement arg2 = arg1.getNextSiblingElement();
                MyXMLElement expr = operation.getFirstChildElement().getNextSiblingElement().getFirstChildElement();
                return new JoinTreeNode(
                        NODE_BUILDER_MAP.get(arg1.getOperationName()).build(connector, arg1),
                        NODE_BUILDER_MAP.get(arg2.getOperationName()).build(connector, arg2),
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

    public static TreeNode parse(AsterixConnector connector, MyXMLElement root) {
        return NODE_BUILDER_MAP.get(root.getOperationName()).build(connector, root);
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

    @Override
    public String translate() {
        return root.translate();
    }

    @Override
    public double getExecutionTime() {
        return root.getExecutionTime();
    }


}
