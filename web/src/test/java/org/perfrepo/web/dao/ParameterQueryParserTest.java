package org.perfrepo.web.dao;

import org.junit.Test;
import org.perfrepo.web.dao.search.AndExpression;
import org.perfrepo.web.dao.search.Expression;
import org.perfrepo.web.dao.search.OrExpression;
import org.perfrepo.web.dao.search.ParameterQueryParser;
import org.perfrepo.web.dao.search.ParameterTerm;
import org.perfrepo.web.dao.search.TagQueryParser;
import org.perfrepo.web.dao.search.Term;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link ParameterQueryParser}.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class ParameterQueryParserTest {

    private ParameterQueryParser parser = new ParameterQueryParser();

    @Test
    public void testBasic() {
        String query = "\"name\":\"value\"";
        Expression expression = parser.process(query);

        assertTrue(expression instanceof ParameterTerm);
        assertEquals("name", ((ParameterTerm) expression).getName());
        assertEquals("value", ((ParameterTerm) expression).getValue());
    }

    @Test
    public void testAnd() {
        String query = "\"name\":\"value\" AND \"name2\":\"value2\"";
        Expression expression = parser.process(query);

        assertTrue(expression instanceof AndExpression);
        AndExpression andExpression = (AndExpression) expression;
        assertEquals("name", ((ParameterTerm) andExpression.getLeftOperand()).getName());
        assertEquals("value", ((ParameterTerm) andExpression.getLeftOperand()).getValue());
        assertEquals("name2", ((ParameterTerm) andExpression.getRightOperand()).getName());
        assertEquals("value2", ((ParameterTerm) andExpression.getRightOperand()).getValue());
    }

    @Test
    public void testOr() {
        String query = "\"name\":\"value\" OR \"name2\":\"value2\"";
        Expression expression = parser.process(query);

        assertTrue(expression instanceof OrExpression);
        OrExpression orExpression = (OrExpression) expression;
        assertEquals("name", ((ParameterTerm) orExpression.getLeftOperand()).getName());
        assertEquals("value", ((ParameterTerm) orExpression.getLeftOperand()).getValue());
        assertEquals("name2", ((ParameterTerm) orExpression.getRightOperand()).getName());
        assertEquals("value2", ((ParameterTerm) orExpression.getRightOperand()).getValue());
    }

    @Test
    public void testCombination() {
        String query = "\"name\":\"value\" AND (\"name2\":\"value2\" OR \"name3\":\"value3\")";
        Expression expression = parser.process(query);

        assertTrue(expression instanceof AndExpression);
        AndExpression andExpression = (AndExpression) expression;
        assertEquals("name", ((ParameterTerm) andExpression.getLeftOperand()).getName());
        assertEquals("value", ((ParameterTerm) andExpression.getLeftOperand()).getValue());

        assertTrue(andExpression.getRightOperand() instanceof OrExpression);
        OrExpression orExpression = (OrExpression) andExpression.getRightOperand();
        assertEquals("name2", ((ParameterTerm) orExpression.getLeftOperand()).getName());
        assertEquals("value2", ((ParameterTerm) orExpression.getLeftOperand()).getValue());
        assertEquals("name3", ((ParameterTerm) orExpression.getRightOperand()).getName());
        assertEquals("value3", ((ParameterTerm) orExpression.getRightOperand()).getValue());
    }

    @Test
    public void testWildcard() {
        String query = "\"name\":\"value*\"";
        Expression expression = parser.process(query);

        assertTrue(expression instanceof ParameterTerm);
        assertEquals("name", ((ParameterTerm) expression).getName());
        assertEquals("value*", ((ParameterTerm) expression).getValue());
    }
}
