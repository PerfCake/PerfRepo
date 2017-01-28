package org.perfrepo.web.dao.search;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

/**
 * Parser for parameter query language into expression object.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class ParameterQueryParser {

    private static final String AND = ParameterSearchParser.tokenNames[ParameterSearchParser.AND];
    private static final String OR = ParameterSearchParser.tokenNames[ParameterSearchParser.OR];

    public Expression process(String query) {
        Tree tree = parseTree(query);
        return processExpression(tree.getChild(0));
    }

    private Expression processExpression(Tree root) {
        if (root.getText().equalsIgnoreCase(AND)) {
            AndExpression andExpression = new AndExpression();
            andExpression.setLeftOperand(processExpression(root.getChild(0)));
            andExpression.setRightOperand(processExpression(root.getChild(1)));
            return andExpression;
        } else if (root.getText().equalsIgnoreCase(OR)) {
            OrExpression orExpression = new OrExpression();
            orExpression.setLeftOperand(processExpression(root.getChild(0)));
            orExpression.setRightOperand(processExpression(root.getChild(1)));
            return orExpression;
        } else {
            return new ParameterTerm(root.getChild(0).getText(), root.getChild(1).getText());
        }
    }

    private CommonTree parseTree(String string) {
        //lexer splits input into tokens
        ANTLRStringStream input = new ANTLRStringStream(string);
        TokenStream tokens = new CommonTokenStream(new ParameterSearchLexer(input));

        //parser generates abstract syntax tree
        ParameterSearchParser parser = new ParameterSearchParser(tokens);

        ParameterSearchParser.query_return ret;
        try {
            ret = parser.query();
        } catch (RecognitionException ex) {
            throw new IllegalStateException("Recognition exception is never thrown, only declared.");
        }

        return (CommonTree) ret.tree;
    }

}
