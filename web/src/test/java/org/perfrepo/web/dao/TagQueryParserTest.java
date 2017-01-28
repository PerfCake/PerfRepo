package org.perfrepo.web.dao;

import org.junit.Test;
import org.perfrepo.web.dao.search.AndExpression;
import org.perfrepo.web.dao.search.Expression;
import org.perfrepo.web.dao.search.OrExpression;
import org.perfrepo.web.dao.search.TagQueryParser;
import org.perfrepo.web.dao.search.Term;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for TagQueryParser.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TagQueryParserTest {

    private TagQueryParser parser = new TagQueryParser();

    @Test
    public void testBasic() {
        String query = "tag";
        Expression expression = parser.process(query);

        assertTrue(expression instanceof Term);
        assertEquals("tag", ((Term) expression).getValue());
    }

    @Test
    public void testBasicMultiple() {
        String query = "tag tag2 -tag3";
        Expression expression = parser.process(query);

        // by default it's there is right associativity
        assertTrue(expression instanceof AndExpression);
        AndExpression andExpression1 = (AndExpression) expression;
        assertEquals("-tag3", ((Term) andExpression1.getRightOperand()).getValue());

        AndExpression andExpression2 = (AndExpression) andExpression1.getLeftOperand();
        assertEquals("tag", ((Term) andExpression2.getLeftOperand()).getValue());
        assertEquals("tag2", ((Term) andExpression2.getRightOperand()).getValue());
    }

    @Test
    public void testAnd() {
        String query = "tag AND tag2";
        Expression expression = parser.process(query);

        assertTrue(expression instanceof AndExpression);
        AndExpression andExpression = (AndExpression) expression;
        assertEquals("tag", ((Term) andExpression.getLeftOperand()).getValue());
        assertEquals("tag2", ((Term) andExpression.getRightOperand()).getValue());
    }

    @Test
    public void testOr() {
        String query = "tag OR tag2";
        Expression expression = parser.process(query);

        assertTrue(expression instanceof OrExpression);
        OrExpression orExpression = (OrExpression) expression;
        assertEquals("tag", ((Term) orExpression.getLeftOperand()).getValue());
        assertEquals("tag2", ((Term) orExpression.getRightOperand()).getValue());
    }

    @Test
    public void testCombination() {
        String query = "tag AND (tag2 OR tag3)";
        Expression expression = parser.process(query);

        assertTrue(expression instanceof AndExpression);
        AndExpression andExpression = (AndExpression) expression;
        assertEquals("tag", ((Term) andExpression.getLeftOperand()).getValue());

        assertTrue(andExpression.getRightOperand() instanceof OrExpression);
        OrExpression orExpression = (OrExpression) andExpression.getRightOperand();
        assertEquals("tag2", ((Term) orExpression.getLeftOperand()).getValue());
        assertEquals("tag3", ((Term) orExpression.getRightOperand()).getValue());
    }

    @Test
    public void testExclusion() {
        String query = "tag AND -tag2";
        Expression expression = parser.process(query);

        assertTrue(expression instanceof AndExpression);
        AndExpression andExpression = (AndExpression) expression;
        assertEquals("tag", ((Term) andExpression.getLeftOperand()).getValue());
        assertEquals("-tag2", ((Term) andExpression.getRightOperand()).getValue());
    }
}
