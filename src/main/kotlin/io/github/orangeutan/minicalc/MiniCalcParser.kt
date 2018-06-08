package io.github.orangeutan.minicalc

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import java.net.URL

class MiniCalcParser {
    companion object {
        @JvmStatic

        fun parse(code: String, savePos: Boolean = true): MiniCalcFile {
            return MiniCalcAntlrParser(CommonTokenStream(MiniCalcAntlrLexer(CharStreams.fromString(code))))
                    .miniCalcFile().toAST(savePos)
        }

        fun parseResource(url: URL, savePos: Boolean = true): MiniCalcFile {
            return MiniCalcAntlrParser(CommonTokenStream(MiniCalcAntlrLexer(CharStreams.fromStream(url.openStream()))))
                    .miniCalcFile().toAST(savePos)
        }

        fun parseResource(resName: String, savePos: Boolean = true): MiniCalcFile {
            return parseResource(MiniCalcParser::class.java.classLoader.getResource(resName), savePos)
        }
    }
}

fun Token.startPoint() = Point(this.line, this.charPositionInLine)

fun Token.endPoint() = Point(this.line, this.charPositionInLine)

fun ParserRuleContext.toPosition(savePos: Boolean): Position? {
    return if (savePos) Position(this.start.startPoint(), this.stop.endPoint())
           else null
}

fun MiniCalcAntlrParser.MiniCalcFileContext.toAST(savePos: Boolean = false): MiniCalcFile
        = MiniCalcFile(
        this.line().map({ it.statement().toAST(savePos) }),
        this.toPosition(savePos))

fun MiniCalcAntlrParser.StatementContext.toAST(savePos: Boolean = false): Statement {
    when(this) {
        is MiniCalcAntlrParser.VarDeclarationStatementContext ->
            return VarDeclaration(
                    this.varDeclaration().assignment().ID().text,
                    this.varDeclaration().assignment().expression().toAST(savePos),
                    this.toPosition(savePos))
        is MiniCalcAntlrParser.AssignmentStatementContext ->
            return Assignment(ReferenceByName(
                    this.assignment().ID().text),
                    this.assignment().expression().toAST(savePos),
                    this.toPosition(savePos))
        is MiniCalcAntlrParser.PrintStatementContext ->
            return Print(
                    this.print().expression().toAST(savePos),
                    this.toPosition(savePos))
        is MiniCalcAntlrParser.InputDeclarationStatementContext ->
            return InputDeclaration(
                    this.inputDeclaration().ID().text,
                    this.inputDeclaration().type().toAST(savePos),
                    this.toPosition(savePos))
        else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
    }
}

fun MiniCalcAntlrParser.ExpressionContext.toAST(savePos: Boolean = false): Expression {
    when(this) {
        is MiniCalcAntlrParser.BinOpContext -> return this.toAST(savePos)
        is MiniCalcAntlrParser.IntLitContext -> return IntLit(this.text, this.toPosition(savePos))
        is MiniCalcAntlrParser.DecLitContext -> return DecLit(this.text, this.toPosition(savePos))
        is MiniCalcAntlrParser.StrLitContext ->
            return StrLit(this.parts.map({ it.toAST(savePos) }),
                    this.toPosition(savePos))
        is MiniCalcAntlrParser.ParenExprContext -> return this.expression().toAST(savePos)
        is MiniCalcAntlrParser.SymbolRefContext -> return SymbolRef(ReferenceByName(this.text),
                this.toPosition(savePos))
        is MiniCalcAntlrParser.TypeConversionContext ->
            return TypeConversion(this.expression().toAST(savePos),
                    this.targetType.toAST(savePos),
                    this.toPosition(savePos))
        else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
    }
}

fun MiniCalcAntlrParser.StringLiteralContentContext.toAST(savePos: Boolean = false): StrLitContent {
    when(this) {
        is MiniCalcAntlrParser.StrLitConstContentContext -> return StrLitConstContent(this.STR_CONTENT().text,
                this.toPosition(savePos))

        is MiniCalcAntlrParser.StrLitInterpContentContext ->
            return StrLitInterpContent(this.expression().toAST(savePos),
                    this.toPosition(savePos))
        else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
    }
}

fun MiniCalcAntlrParser.TypeContext.toAST(savePos: Boolean = false): Type {
    when(this) {
        is MiniCalcAntlrParser.IntTypeContext -> return IntType(this.toPosition(savePos))
        is MiniCalcAntlrParser.DecTypeContext -> return DecType(this.toPosition(savePos))
        else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
    }
}

fun MiniCalcAntlrParser.BinOpContext.toAST(savePos: Boolean = false): Expression {
    when(this.operator.text) {
        "+" -> return AdditionExpr(this.left.toAST(savePos),
                this.right.toAST(savePos),
                this.toPosition(savePos))
        "-" -> return SubtractionExpr(this.left.toAST(savePos),
                this.right.toAST(savePos),
                this.toPosition(savePos))
        "*" -> return MultiplicationExpr(this.left.toAST(savePos),
                this.right.toAST(savePos),
                this.toPosition(savePos))
        "/" -> return DivisionExpr(this.left.toAST(savePos),
                this.right.toAST(savePos),
                this.toPosition(savePos))
        else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
    }
}