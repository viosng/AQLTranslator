package parser.nodes.impl;

import parser.nodes.TreeNode;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: viosng
 * Date: 30.12.13
 * Time: 8:36
 */
public class DatasetTreeNode extends TreeNode {

    private String dataset;

    public DatasetTreeNode(String dataset, List<String> fieldNames) {
        super();
        setFieldNames(fieldNames);
        this.dataset = dataset;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    @Override
    public String translate() {
        return String.format("dataset %s", dataset);
    }

    @Override
    public String toString() {
        return "DatasetTreeNode{" +
                "level=" + getLevel() +
                ", var='" + getVar() + '\'' +
                ", from=" + getFrom() +
                ", fieldNames=" + getFieldNames() +
                ", dataset='" + dataset + '\'' +
                '}';
    }
}
