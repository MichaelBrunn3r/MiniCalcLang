package io.github.orangeutan.minicalc;

import kotlin.test.assertEquals
import org.junit.Test

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import java.util.LinkedList;

import io.github.orangeutan.minicalc.MiniCalcLexer;

fun lexerForStr(str: String) = MiniCalcLexer(CharStreams.fromString(str))

fun tokenContents(lexer: MiniCalcLexer): List<String> {
    val tokens = LinkedList<String>()
    do {
        val token = lexer.nextToken()
        when(token.type) {
            -1   -> tokens.add("EOF")
            else -> if(token.type != MiniCalcLexer.WS) tokens.add(lexer.text)
        }
    } while (token.type != -1)
    return tokens
}

fun tokenNames(lexer: MiniCalcLexer) : List<String> {
    val tokens = LinkedList<String>()
    do {
        val token = lexer.nextToken()
        when(token.type) {
            -1   -> tokens.add("EOF")
            else -> if(token.type != MiniCalcLexer.WS) tokens.add(MiniCalcLexer.VOCABULARY.getSymbolicName(token.type))
        }
    } while (token.type != -1)
    return tokens
}

class MiniCalcLexerTest {
    @Test
    fun parseVarDeclarationAssignedAnIntegerLiteral() {
        assertEquals(listOf("VAR", "ID", "ASSIGN", "INTLIT", "EOF"),
                            tokenNames(lexerForStr("var a = 1")))
    }

    @Test
    fun parseVarDeclarationAssignedADecimalLiteral() {
        assertEquals(listOf("VAR", "ID", "ASSIGN", "DECLIT", "EOF"),
                            tokenNames(lexerForStr("var a = 1.23")))
    }

    @Test
    fun parseVarDeclarationAssignedASum() {
        assertEquals(listOf("VAR", "ID", "ASSIGN", "INTLIT", "PLUS", "INTLIT", "EOF"),
                            tokenNames(lexerForStr("var a = 1 + 2")))
    }

    @Test
    fun parseMathematicalExpression() {
        assertEquals(listOf("INTLIT", "PLUS", "ID", "ASTERISK", "INTLIT", "DIVISION", "INTLIT", "MINUS", "INTLIT", "EOF"),
                            tokenNames(lexerForStr("1 + a * 3 / 4 - 5")))
    }

    @Test
    fun parseCast() {
        assertEquals(listOf("ID", "ASSIGN", "ID", "AS", "INT", "EOF"),
                            tokenNames(lexerForStr("a = b as Int")))
    }

    @Test
    fun parseSimpleString() {
        assertEquals(listOf("STR_OPEN", "STR_CONTENT", "STR_CLOSE", "EOF"),
                            tokenNames(lexerForStr("\"hi!\"")))
    }

    @Test
    fun parseStringWithEscapedChars() {
        var escChars = arrayOf(
                        arrayOf("ESC_NEWLINE", "\\n"), 
                        arrayOf("ESC_SLASH", "\\\\"), 
                        arrayOf("ESC_STR_DELIMITER", "\\\""), 
                        arrayOf("ESC_SHARP", "\\#"))

        for(c in escChars) {
            var code = "\"hi!" + c[1] + "\""
            assertEquals(listOf("\"", "hi!", c[1], "\"", "EOF"),
                         tokenContents(lexerForStr(code)))
            assertEquals(listOf("STR_OPEN", "STR_CONTENT", c[0], "STR_CLOSE", "EOF"),
                         tokenNames(lexerForStr(code)))
        }
    }

    @Test
    fun parseStringWithInterpolation() {
        var code = "\"hi #{name}. This is a number: #{5 * 4}\""
        assertEquals(listOf("\"", "hi ", "#{", "name", "}", ". This is a number: ", "#{", "5", "*", "4", "}", "\"", "EOF"),
                     tokenContents(lexerForStr(code)))
        assertEquals(listOf("STR_OPEN", "STR_CONTENT", "INTERP_OPEN", "ID", "INTERP_CLOSE", "STR_CONTENT", 
                             "INTERP_OPEN", "INTLIT", "ASTERISK", "INTLIT", "INTERP_CLOSE", "STR_CLOSE", "EOF"),
                     tokenNames(lexerForStr(code)))
    }
}