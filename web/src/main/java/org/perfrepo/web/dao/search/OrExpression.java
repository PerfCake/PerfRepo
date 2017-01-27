package org.perfrepo.web.dao.search;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class OrExpression extends BinaryExpression {

    @Override
    public String toString() {
        return "OrExpression{" +
                "leftOperand=" + getLeftOperand() +
                ", rightOperand=" + getRightOperand() +
                '}';
    }

}
