package org.perfrepo.web.dao.search;

/**
 * Abstraction for AND expression.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class AndExpression extends BinaryExpression {

    @Override
    public String toString() {
        return "AndExpression{" +
                "leftOperand=" + getLeftOperand() +
                ", rightOperand=" + getRightOperand() +
                '}';
    }

}
