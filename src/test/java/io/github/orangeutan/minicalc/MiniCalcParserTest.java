package io.github.orangeutan.minicalc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.io.IOUtils;

import io.github.orangeutan.minicalc.MiniCalcLexer;

public class MiniCalcParserTest
{

    // Utilities

    public static ParseTreeNode toParseTree(ParserRuleContext node) {
        String nodeName = node.getClass().getSimpleName();
        nodeName = nodeName.substring(0,nodeName.lastIndexOf("Context"));
        ParseTreeNode ptNode = new ParseTreeNode(nodeName);
        for(ParseTree child : node.children) {
            if(child instanceof ParserRuleContext) 
                ptNode.addChild(toParseTree((ParserRuleContext)child));
            else if(child instanceof TerminalNode)
                ptNode.addChild(new ParseTreeLeaf(MiniCalcLexer.VOCABULARY.getSymbolicName(((TerminalNode)child).getSymbol().getType()), child.getText()));
        }
        return ptNode;
    }

    public MiniCalcLexer lexerForResource(String resourceName) throws IOException {
        ClassLoader cl = this.getClass().getClassLoader();
        return new MiniCalcLexer(CharStreams.fromStream(cl.getResourceAsStream(resourceName)));
    }

    public MiniCalcParser.MiniCalcFileContext parseResource(String resourceName) {
        try {
			return new MiniCalcParser(new CommonTokenStream(lexerForResource(resourceName))).miniCalcFile();
		} catch (RecognitionException | IOException e) {
			e.printStackTrace();
        }
        return null;
    }

    void testResource(String fileNameExpected, String fileNameActual, String encoding) {
        String expected = "";
		try {
			expected = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(fileNameExpected), encoding);
		} catch (IOException e) {
			e.printStackTrace();
        }
        String actual = toParseTree(parseResource(fileNameActual)).multiLineString();
        for(int i=0; i<actual.length(); i++) {
            if(expected.charAt(i) != actual.charAt(i)) {
                System.out.printf("Strings differ at position %d\n\n\n", i);
                break;
            }
        }
        assertEquals(expected, actual);
    }

    @Test
    void testAdditionAssignment() {
        testResource("parserTest/additionAssignment.expected", "parserTest/additionAssignment.mc", "UTF-8");        
    } 

    @Test
    void testSimpleVarDecl() {
        testResource("parserTest/simpleVarDecl.expected", "parserTest/simpleVarDecl.mc", "UTF-8");
    }

    @Test
    void testPrecedenceExpression() {
        testResource("parserTest/precedenceExpression.expected", "parserTest/precedenceExpression.mc", "UTF-8");
    }
}