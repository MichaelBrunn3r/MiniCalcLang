package io.github.orangeutan.minicalc

import org.junit.Test
import kotlin.test.assertEquals

class TypeSystemTest {

    @Test
    fun testLiterals() {
        val ast = MiniCalcParser.parseResource("typeSystemTest/literals.mc")
        ast.resolveSymbols()

        val varDecl = ast.collectByType(VarDeclaration::class.java)
        assertEquals(5, varDecl.size)
        assertEquals(StrType::class.java, varDecl[0].type()::class.java)
        assertEquals(IntType::class.java, varDecl[1].type()::class.java)
        assertEquals(DecType::class.java, varDecl[2].type()::class.java)
        assertEquals(DecType::class.java, varDecl[3].type()::class.java)
        assertEquals(DecType::class.java, varDecl[4].type()::class.java)
    }

    @Test
    fun testTypeOfStringConcatenation() {
        val ast = MiniCalcParser.parse("var a = \"Hello\" + \"Jo\"")
        ast.resolveSymbols()

        val varDecl = ast.collectByType(VarDeclaration::class.java)
        assertEquals(1, varDecl.size)
        assertEquals("a", varDecl[0].name)
        assertEquals(StrType::class.java, varDecl[0].type()::class.java)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidStrConcatenation() {
        val ast = MiniCalcParser.parse("var a = 10 + \"Hello\"")
        ast.resolveSymbols()

        val varDecl = ast.collectByType(VarDeclaration::class.java)
        assertEquals(1, varDecl.size)
        varDecl[0].type()
    }

    @Test
    fun testTypeConversion() {
        val ast = MiniCalcParser.parse("var a = 10 as Decimal")
        ast.resolveSymbols()

        val typeConv = ast.collectByType(TypeConversion::class.java)
        assertEquals(1, typeConv.size)
        assertEquals(DecType::class.java, typeConv[0].type()::class.java)
    }
}