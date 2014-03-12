package parser.nodes.impl;

import parser.nodes.Node;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: viosng
 * Date: 30.12.13
 * Time: 8:36
 */
public class DatasetNode extends Node {

    private String dataset;

    public DatasetNode(String dataset, List<String> fieldNames) {
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
        return "DatasetNode{" +
                "level=" + getLevel() +
                ", var='" + getVar() + '\'' +
                ", from=" + getFrom() +
                ", fieldNames=" + getFieldNames() +
                ", dataset='" + dataset + '\'' +
                '}';
    }
}
