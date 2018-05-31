package io.github.orangeutan.minicalc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import io.github.orangeutan.minicalc.MiniCalcLexer;

public class MiniCalcLexerTest
{
    public static MiniCalcLexer lexerForCode(String code) {
        return new MiniCalcLexer(CharStreams.fromString(code));
    }

    public static List<String> tokensContent(MiniCalcLexer lexer) {
        LinkedList<String> tokens = new LinkedList<>();
        Token token;
        do { 
            token = lexer.nextToken();
            if(token.getType() == -1) {
                tokens.add("EOF");
            } else if(token.getType() != MiniCalcLexer.WS) tokens.add(lexer.getText());
        } while (token.getType() != -1);
        return tokens;
    }

    public static List<String> tokensNames(MiniCalcLexer lexer) {
        LinkedList<String> tokens = new LinkedList<>();
        Token token;
        do {
            token = lexer.nextToken();
            if(token.getType() == -1) {
                tokens.add("EOF");
            } else if(token.getType() != MiniCalcLexer.WS) tokens.add(MiniCalcLexer.VOCABULARY.getSymbolicName(token.getType()));
        } while (token.getType() != -1);
        return tokens;
    }

    @Test
    void parseVarDeclarationAssignedAnIntegerLiteral() {
        assertEquals(Arrays.asList("VAR", "ID", "ASSIGN", "INTLIT", "EOF"), tokensNames(lexerForCode("var a = 1")));
    }

    @Test
    void parseVarDeclarationAssignedADecimalLiteral() {
        assertEquals(Arrays.asList("VAR", "ID", "ASSIGN", "DECLIT", "EOF"), tokensNames(lexerForCode("var a = 1.23")));
    }

    @Test
    void parseVarDeclarationAssignedASum() {
        assertEquals(Arrays.asList("VAR", "ID", "ASSIGN", "INTLIT", "PLUS", "INTLIT", "EOF"), tokensNames(lexerForCode("var a = 1 + 2")));
    }
    
    @Test
    void parseMathematicalExpression() {
        assertEquals(Arrays.asList("INTLIT", "PLUS", "ID", "ASTERISK", "INTLIT", "DIVISION", "INTLIT", "MINUS", "INTLIT", "EOF"),
                     tokensNames(lexerForCode("1 + a * 3 / 4 - 5")));
    }

    @Test
    void parseCast() {
        assertEquals(Arrays.asList("ID", "ASSIGN", "ID", "AS", "INT", "EOF"), tokensNames(lexerForCode("a = b as Int")));
    }

    @Test
    void parseSimpleString() {
        assertEquals(Arrays.asList("STR_OPEN", "STR_CONTENT", "STR_CLOSE", "EOF"), tokensNames(lexerForCode("\"hi!\"")));
    }

    @Test
    void parseStringWithEscapedChars() {
        String[][] escChars = {{"ESC_NEWLINE", "\\n"}, {"ESC_SLASH", "\\\\"}, {"ESC_STR_DELIMITER", "\\\""}, {"ESC_SHARP", "\\#"}};
        for(int i=0; i<escChars.length; i++) {
            String code = "\"hi!" + escChars[i][1] + "\"";
            assertEquals(Arrays.asList("\"", "hi!", escChars[i][1], "\"","EOF"), tokensContent(lexerForCode(code)));
            assertEquals(Arrays.asList("STR_OPEN", "STR_CONTENT", escChars[i][0], "STR_CLOSE", "EOF"), tokensNames(lexerForCode(code)));
        }
    }

    @Test
    void parseStringWithInterpolation() {
        String code = "\"hi #{name}. This is a number: #{5 * 4}\"";
        assertEquals(Arrays.asList("\"", "hi ", "#{", "name", "}", ". This is a number: ", "#{", "5", "*", "4", "}", "\"", "EOF"),
                     tokensContent(lexerForCode(code)));
        assertEquals(Arrays.asList("STR_OPEN", "STR_CONTENT", "INTERP_OPEN", "ID", "INTERP_CLOSE", "STR_CONTENT", 
                                    "INTERP_OPEN", "INTLIT", "ASTERISK", "INTLIT", "INTERP_CLOSE", "STR_CLOSE", "EOF"),
                     tokensNames(lexerForCode(code)));
    }
}