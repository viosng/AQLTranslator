package parser.nodes.impl;

import parser.nodes.ContainerNode;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: viosng
 * Date: 30.12.13
 * Time: 8:36
 */
public class DatasetNode extends ContainerNode {

    private String dataset;

    public DatasetNode(String dataset, List<String> fieldNames) {
        this.dataset = dataset;
        this.fieldNames = fieldNames;
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
}
