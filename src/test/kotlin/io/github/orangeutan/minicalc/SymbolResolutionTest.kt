package io.github.orangeutan.minicalc

import org.junit.Test
import kotlin.test.assertEquals

class SymbolResolutionTest {

    @Test
    fun testResolveVarRefToVariableDeclaredBefore() {
        val ast = MiniCalcParser.parseResource("symbolTest/resolveRefToVarDeclaredBefore.mc")
        ast.resolveSymbols()

        val varRefs = ast.collectByType(VarRef::class.java)
        assertEquals(1, varRefs.size)
        assertEquals(true, varRefs[0].reference.isResolved)
        assertEquals("a", varRefs[0].reference.name)
    }

    @Test
    fun resolveVarRefToVarDeclaredOnSameLine() {
        val ast = MiniCalcParser.parseResource("symbolTest/resolveRefToVarDeclaredOnSameLine.mc")
        ast.resolveSymbols()

        val varRefs = ast.collectByType(VarRef::class.java)
        assertEquals(1, varRefs.size)
        assertEquals(false, varRefs[0].reference.isResolved)
    }

    @Test
    fun resolveVarRefToVarDeclaredAfter() {
        val ast = MiniCalcParser.parseResource("symbolTest/resolveRefToVarDeclaredAfter.mc")
        ast.resolveSymbols()

        val varRefs = ast.collectByType(VarRef::class.java)
        assertEquals(1, varRefs.size)
        assertEquals(false, varRefs[0].reference.isResolved)
    }

    @Test
    fun testResolveVarRefToInputDeclaredBefore() {
        val ast = MiniCalcParser.parseResource("symbolTest/resolveRefToInputDeclaredBefore.mc")
        ast.resolveSymbols()

        val varRefs = ast.collectByType(VarRef::class.java)
        assertEquals(1, varRefs.size)
        assertEquals(true, varRefs[0].reference.isResolved)
        assertEquals("a", varRefs[0].reference.name)
    }

    @Test
    fun resolveVarRefToInputDeclaredAfter() {
        val ast = MiniCalcParser.parseResource("symbolTest/resolveRefToInputDeclaredAfter.mc")
        ast.resolveSymbols()

        val varRefs = ast.collectByType(VarRef::class.java)
        assertEquals(1, varRefs.size)
        assertEquals(false, varRefs[0].reference.isResolved)
    }

    @Test
    fun resolveVarRefToNonexistentVar() {
        val ast = MiniCalcParser.parseResource("symbolTest/resolveRefToNonexistentVar.mc")
        ast.resolveSymbols()

        val varRefs = ast.collectByType(VarRef::class.java)
        assertEquals(1, varRefs.size)
        assertEquals(false, varRefs[0].reference.isResolved)
    }
}