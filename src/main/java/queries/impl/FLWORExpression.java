package queries.impl;

import queries.Expression;

/**
 * Created with IntelliJ IDEA.
 * User: viosng
 * Date: 30.12.13
 * Time: 8:00
 */

/*
FLWOR         ::= ( ForClause | LetClause ) ( Clause )* "return" Expression
Clause         ::= ForClause | LetClause | WhereClause | OrderbyClause
                 | GroupClause | LimitClause | DistinctClause
ForClause      ::= "for" Variable ( "at" Variable )? "in" ( Expression )
LetClause      ::= "let" Variable ":=" Expression
WhereClause    ::= "where" Expression
OrderbyClause  ::= "order" "by" Expression ( ( "asc" ) | ( "desc" ) )?
                   ( "," Expression ( ( "asc" ) | ( "desc" ) )? )*
GroupClause    ::= "group" "by" ( Variable ":=" )? Expression ( "," ( Variable ":=" )? Expression )*
                   "with" VariableRef ( "," VariableRef )*
LimitClause    ::= "limit" Expression ( "offset" Expression )?
DistinctClause ::= "distinct" "by" Expression ( "," Expression )*
Variable       ::= <VARIABLE>
*/

public class FLWORExpression implements Expression {

    private static long varNumber = 0L;

    private String var;
    private Expression from, nestedFor, where, groupBy, orderBy;

    public FLWORExpression() {
        this.var = "$f" + (varNumber++);
    }


    public FLWORExpression var(String var) {
        this.var = var;
        return this;
    }

    public FLWORExpression from(Expression from) {
        this.from = from;
        return this;
    }

    public FLWORExpression where(Expression where) {
        this.where = where;
        return this;
    }

    public FLWORExpression groupBy(Expression groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    public FLWORExpression nestedFor(Expression nestedFor) {
        this.nestedFor = nestedFor;
        return this;
    }

    public FLWORExpression orderBy(Expression orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public Expression getNestedFor() {
        return nestedFor;
    }

    public void setNestedFor(Expression nestedFor) {
        this.nestedFor = nestedFor;
    }

    public Expression getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Expression orderBy) {
        this.orderBy = orderBy;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public Expression getFrom() {
        return from;
    }

    public void setFrom(Expression from) {
        this.from = from;
    }

    public Expression getWhere() {
        return where;
    }

    public void setWhere(Expression where) {
        this.where = where;
    }

    public Expression getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(Expression groupBy) {
        this.groupBy = groupBy;
    }

    @Override
    public String translate(String var) throws UnsupportedOperationException{
        throw new UnsupportedOperationException("FLWOR expression has his own variable.");
    }

    @Override
    public String translate() {
        StringBuilder query = new StringBuilder();
        query.append("(for ");
        query.append(var);
        query.append(" in ");
        query.append(from.translate());
        if (where != null) {
            query.append("\nwhere ");
            query.append(where.translate(var));
        }
        query.append("\nreturn ");
        query.append(var);
        query.append(")");
        return query.toString();
    }



}
