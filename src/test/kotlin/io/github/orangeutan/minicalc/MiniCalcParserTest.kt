package io.github.orangeutan.minicalc;

import kotlin.test.assertEquals
import org.junit.Test

import java.util.LinkedList;
import java.net.URL

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import org.apache.commons.io.IOUtils;

import io.github.orangeutan.minicalc.MiniCalcLexer;
import io.github.orangeutan.minicalc.MiniCalcParser;

abstract class ParseTreeElement {
    abstract fun toMultilineStr(indentation: String = ""): String
}

class ParseTreeLeaf(var type: String, var text: String): ParseTreeElement() {
    override fun toString(): String
        = "'$text'"

    override fun toMultilineStr(indentation: String): String 
        = "$indentation${toString()}" 
}

class ParseTreeNode(var name: String): ParseTreeElement(){
    var children = LinkedList<ParseTreeElement>()

    fun addChild(child: ParseTreeElement): ParseTreeNode {
        children.add(child)
        return this
    }

    override fun toString(): String 
        = "Node($name) $children"

    override fun toMultilineStr(indentation: String): String {
        val sb = StringBuilder()
        sb.append("${indentation}$name\n")
        children.forEach{c -> 
            sb.append(c.toMultilineStr(indentation + "    "))
            if(c is ParseTreeLeaf && c.text != "<EOF>") sb.append("\n")
        }
        return sb.toString()
    }
}

fun getResource(resName: String, cls: Class<Any>): URL 
    = cls.classLoader.getResource(resName)

fun lexerForResource(resource: URL): MiniCalcLexer
    = MiniCalcLexer(CharStreams.fromStream(resource.openStream()))

fun parseResource(resource: URL): MiniCalcParser.MiniCalcFileContext
    = MiniCalcParser(CommonTokenStream(lexerForResource(resource))).miniCalcFile()


fun toParseTree(node: ParserRuleContext): ParseTreeNode {
    val nodeName = node.javaClass.simpleName.removeSuffix("Context")
    val treeNode = ParseTreeNode(nodeName)
    node.children.forEach{ c ->
        when(c) {
            is ParserRuleContext -> treeNode.addChild(toParseTree(c))
            is TerminalNode -> treeNode.addChild(ParseTreeLeaf(MiniCalcLexer.VOCABULARY.getSymbolicName(c.symbol.type), c.text))
        }
    }
    return treeNode
}

class MiniCalcParserTest {

    fun testResource(resExpected: String, resActual: String) {
        val strExpected = String(IOUtils.toByteArray(getResource(resExpected, this.javaClass).openStream()))
        val strActual = toParseTree(parseResource(this.javaClass.classLoader.getResource(resActual))).toMultilineStr()
        assertEquals(strExpected, strActual)
    }

    @Test
    fun testAdditionAssignment() {
        testResource("parserTest/additionAssignment.expected", "parserTest/additionAssignment.mc")
    }

    @Test
    fun testSimpleVarDecl() {
        testResource("parserTest/simpleVarDecl.expected", "parserTest/simpleVarDecl.mc")
    }

    @Test
    fun testPrecedenceExpression() {
        testResource("parserTest/precedenceExpression.expected", "parserTest/precedenceExpression.mc")
    }
}