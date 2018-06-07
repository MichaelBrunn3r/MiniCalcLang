package io.github.orangeutan.minicalc

import kotlin.test.assertEquals
import org.junit.Test

import java.net.URL

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class MiniCalcASTTest {

    fun getResource(resName: String): URL {
        return this.javaClass.classLoader.getResource(resName)
    }

    fun readResource(res: URL): String {
        return res.openStream().bufferedReader().use { it.readText() }
    }

    fun lexerForResource(resource: URL): MiniCalcAntlrLexer
        = MiniCalcAntlrLexer(CharStreams.fromStream(resource.openStream()))

    fun parseResource(resourceName: String): MiniCalcAntlrParser.MiniCalcFileContext {
        return MiniCalcAntlrParser(CommonTokenStream(lexerForResource(getResource(resourceName)))).miniCalcFile()
    }

    @Test
    fun testToMultinineString() {
        /** Test if the Method ASTNode.toMultilineString works correclty */
        val ast =
                MiniCalcFile(listOf<Statement>(
                    VarDeclaration("a",
                        AdditionExpr(
                            IntLit("1", Position(1,8,1,8)),
                            IntLit("2", Position(1,12,1,12)),
                            Position(1,8,1,12)),
                        Position(1,0,1,12)),
                    Assignment(ReferenceByName("a"),
                        MultiplicationExpr(
                            IntLit("7", Position(2,4,2,4)),
                            DivisionExpr(
                                IntLit("2", Position(2,9,2,9)),
                                IntLit("3", Position(2,11,2,11)),
                                Position(2,9,2,11)),
                            Position(2,4,2,12)),
                        Position(2,0,2,12))),
                    Position(1,0,2,13))

        var expectedStr = readResource(getResource("astTest/toMultilineString"))

        assertEquals(expectedStr, ast.toMultilineStr())
    }

    @Test
    fun testFindNearestAncestor() {
        /** Test if the Method ASTNode.findNearestAncestor works correctly */
        val intlit = IntLit("1", Position(1,8,1,8))
        val varDecl =
            VarDeclaration("a",
                AdditionExpr(
                    intlit,
                    IntLit("2", Position(1,12,1,12)),
                    Position(1,8,1,12)
                ),
                Position(1,0,1,12)
            )

        val ast =
                MiniCalcFile(listOf<Statement>(
                        varDecl
                    ),
                    Position(1,0,2,13))

        assertEquals(ast.findNearestAncestor(ASTNode::class.java), null)
        assert(intlit.findNearestAncestor(VarDeclaration::class.java)!!.equals(varDecl))
        assert(intlit.findNearestAncestor(MiniCalcFile::class.java)!!.equals(ast))
    }

    @Test
    fun testRenameVar() {
        val ast = MiniCalcFile(listOf<Statement>(
            VarDeclaration("A", IntLit("10")),
            Assignment(ReferenceByName("A"), IntLit("11")),
            Print(IDRef(ReferenceByName("A")))
        ))

        val expectedTransformedAST = MiniCalcFile(listOf<Statement>(
            VarDeclaration("B", IntLit("10")),
            Assignment(ReferenceByName("B"), IntLit("11")),
            Print(IDRef(ReferenceByName("B")))
        ))

        ast.execOnAST({
            when(it) {
                is VarDeclaration -> it.name = "B"
                is IDRef -> it.varName.name = "B"
                is Assignment -> it.varDecl.name = "B"
            }})
        assertEquals(expectedTransformedAST, ast)
    }

    @Test
    fun testVarDeclarationWithPos() {
        val expectedASTWithoutPos =
            MiniCalcFile(listOf<Statement>(
                VarDeclaration("a",
                    IntLit("1"))
            ))
        val expectedASTWith =
            MiniCalcFile(listOf<Statement>(
                VarDeclaration("a",
                    IntLit("1", Position(1,8,1,8)),
                    Position(1,0,1,8))),
                Position(1,0,1,9))

        val astWithoutPos = parseResource("astTest/varDeclaration.mc").toAST(savePos=false)
        val astWithPos = parseResource("astTest/varDeclaration.mc").toAST(savePos= true)

        assertEquals(expectedASTWithoutPos, astWithoutPos)
        assertEquals(expectedASTWith, astWithPos)
    }


    @Test
    fun testSimpleFile() {
        val expectedASTWithoutPos =
            MiniCalcFile(listOf<Statement>(
                VarDeclaration("a",
                    AdditionExpr(
                        IntLit("1"),
                        IntLit("2")
                    )
                ),
                Assignment(ReferenceByName("a"),
                    MultiplicationExpr(
                        IntLit("7"),
                        DivisionExpr(
                           IntLit("2"),
                            IntLit("3")
                        )
                    )
                )
            ))
        val expectedASTWithPos =
            MiniCalcFile(listOf<Statement>(
                VarDeclaration("a",
                    AdditionExpr(
                        IntLit("1", Position(1,8,1,8)),
                        IntLit("2", Position(1,12,1,12)),
                        Position(1,8,1,12)),
                    Position(1,0,1,12)),
                Assignment(ReferenceByName("a"),
                    MultiplicationExpr(
                        IntLit("7", Position(2,4,2,4)),
                        DivisionExpr(
                            IntLit("2", Position(2,9,2,9)),
                            IntLit("3", Position(2,11,2,11)),
                            Position(2,9,2,11)),
                        Position(2,4,2,12)),
                    Position(2,0,2,12))),
                Position(1,0,2,13))

        val astWithoutPos = parseResource("astTest/simpleFile.mc").toAST(savePos= false)
        val astWithPos = parseResource("astTest/simpleFile.mc").toAST(savePos= true)

        assertEquals(expectedASTWithoutPos, astWithoutPos)
        assertEquals(expectedASTWithPos, astWithPos)
    }

    @Test
    fun testCastNumbers() {
        val expectedAST =
            MiniCalcFile(listOf<Statement>(
                Assignment(ReferenceByName("a"),
                    TypeConversion(
                        IntLit("7"),
                        IntType()
                    )
                ),
                Assignment(ReferenceByName("b"),
                    TypeConversion(
                        DecLit("2.0"),
                        DecType()
                    )
                )
            ))
        val ast = parseResource("astTest/castNumbers.mc").toAST(savePos= false)

        assertEquals(expectedAST, ast)
    }

    @Test
    fun testPrint() {
        val expectedAST =
            MiniCalcFile(listOf<Statement>(
                Print(IDRef(ReferenceByName("a")))
            ))
        val ast = parseResource("astTest/print.mc").toAST(savePos= false)

        assertEquals(expectedAST, ast)
    }
}