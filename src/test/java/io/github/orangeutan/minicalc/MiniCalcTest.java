package io.github.orangeutan.minicalc;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import io.github.orangeutan.minicalc.MiniCalcLexer;

public class MiniCalcTest
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
    public static void parseVarDeclarationAssignedAnIntegerLiteral() {
        assertEquals(Arrays.asList("VAR", "ID", "ASSIGN", "INTLIT", "EOF"), tokensNames(lexerForCode("var a = 1")));
    }

    @Test
    public static void parseVarDeclarationAssignedADecimalLiteral() {
        assertEquals(Arrays.asList("VAR", "ID", "ASSIGN", "DECLIT", "EOF"), tokensNames(lexerForCode("var a = 1.23")));
    }
    
}