package io.github.orangeutan.minicalc;

import java.io.IOException;
import java.nio.file.Paths;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import io.github.orangeutan.minicalc.MiniCalcLexer;

public class MiniCalc
{
    public void printTokens(String filepath) {
        MiniCalcLexer lexer;
        try {
            lexer = new MiniCalcLexer(CharStreams.fromPath(Paths.get(filepath)));

            Token token;
            do {
                token = lexer.nextToken();
                String typeName = MiniCalcLexer.VOCABULARY.getSymbolicName(token.getType());
                String text = token.getText().replaceAll("\n", "\\n").replaceAll("\r", "\\r");
                System.out.printf("[%d:%d-%d]\'%s\':%s\n", token.getLine(), token.getStartIndex(), token.getStopIndex(), typeName, text);
            } while(token.getType() != -1);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main( String[] args )
    {
        MiniCalc miniCalc = new MiniCalc();
        miniCalc.printTokens("../examples/rectangle.mc");
    }
}