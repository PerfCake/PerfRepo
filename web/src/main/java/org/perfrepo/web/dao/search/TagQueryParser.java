package org.perfrepo.web.dao.search;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

/**
 * Parser for tag query language into expression object.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TagQueryParser {

    private static final String AND = TagSearchParser.tokenNames[TagSearchParser.AND];
    private static final String OR = TagSearchParser.tokenNames[TagSearchParser.OR];

    public Expression process(String query) {
        if (!query.contains(AND) && !query.contains(OR)) { //just basic query with tags like "tag1 tag2 tag3"
            query = String.join(" AND ", query.split(" "));
        }

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
            return new Term(root.getText());
        }
    }

    private CommonTree parseTree(String string) {
        //lexer splits input into tokens
        ANTLRStringStream input = new ANTLRStringStream(string);
        TokenStream tokens = new CommonTokenStream(new TagSearchLexer(input));

        //parser generates abstract syntax tree
        TagSearchParser parser = new TagSearchParser(tokens);

        TagSearchParser.query_return ret;
        try {
            ret = parser.query();
        } catch (RecognitionException ex) {
            throw new IllegalStateException("Recognition exception is never thrown, only declared.");
        }

        return (CommonTree) ret.tree;
    }

}
