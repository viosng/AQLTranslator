package queries.impl;

import queries.Expression;

/**
 * Created with IntelliJ IDEA.
 * User: viosng
 * Date: 30.12.13
 * Time: 8:36
 */
public class DatasetExpression implements Expression {

    private String dataset;

    public DatasetExpression(String dataset) {
        this.dataset = dataset;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    @Override
    public String translate(String var) throws UnsupportedOperationException{
        throw new UnsupportedOperationException("Dataset expression doesn't need variable.");
    }

    @Override
    public String translate() {
        return String.format("(dataset %s)", dataset);
    }
}
