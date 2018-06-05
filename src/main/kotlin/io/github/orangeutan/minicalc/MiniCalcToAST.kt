package io.github.orangeutan.minicalc

import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.ParserRuleContext

import io.github.orangeutan.minicalc.MiniCalcParser.*

fun Token.startPoint() = Point(this.line, this.charPositionInLine)

fun Token.endPoint() = Point(this.line, this.charPositionInLine)

fun ParserRuleContext.toPosition(savePosition: Boolean): Position? {
    if (savePosition) {
        return Position(this.start.startPoint(), this.stop.endPoint())
    } else return null
}

fun MiniCalcFileContext.toAST(savePos: Boolean = false): MiniCalcFile
    = MiniCalcFile(
        this.line().map({ it.statement().toAST(savePos) }),
        this.toPosition(savePos))

fun StatementContext.toAST(savePos: Boolean = false): Statement {
    when(this) {
        is VarDeclarationStatementContext -> 
            return VarDeclaration(
                       this.varDeclaration().assignment().ID().text,
                       this.varDeclaration().assignment().expression().toAST(savePos),
                       this.toPosition(savePos))
        is AssignmentStatementContext -> 
            return Assignment(ReferenceByName(
                       this.assignment().ID().text),
                       this.assignment().expression().toAST(savePos),
                       this.toPosition(savePos))
        is PrintStatementContext -> 
            return Print(
                       this.print().expression().toAST(savePos),
                       this.toPosition(savePos))
        is InputDeclarationStatementContext -> 
            return InputDeclaration(
                       this.inputDeclaration().ID().text,
                       this.inputDeclaration().type().toAST(savePos),
                       this.toPosition(savePos))
        else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
    }
}

fun ExpressionContext.toAST(savePos: Boolean = false): Expression {
    when(this) {
        is BinOpContext -> return this.toAST(savePos)
        is IntLitContext -> return IntLit(this.text, this.toPosition(savePos))
        is DecLitContext -> return DecLit(this.text, this.toPosition(savePos))
        is StrLitContext -> 
            return StrLit(this.parts.map({ it.toAST(savePos) }),
                              this.toPosition(savePos))
        is ParenExprContext -> return this.expression().toAST(savePos)
        is IDRefContext -> return IDRef(ReferenceByName(this.text),
                                                       this.toPosition(savePos))
        is TypeConversionContext -> 
            return TypeConversion(this.expression().toAST(savePos),
                                   this.targetType.toAST(savePos),
                                   this.toPosition(savePos))
        else -> throw UnsupportedOperationException(this.javaClass.canonicalName)    
    }
}

fun StringLiteralContentContext.toAST(savePos: Boolean = false): StrLitContent {
    when(this) {
        is StrLitConstContentContext -> return StrLitConstContent(this.STR_CONTENT().text,
                                                                   this.toPosition(savePos))

        is StrLitInterpContentContext -> 
            return StrLitInterpContent(this.expression().toAST(savePos),
                                        this.toPosition(savePos))
        else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
    }
}

fun TypeContext.toAST(savePos: Boolean = false): Type {
    when(this) {
        is IntTypeContext -> return IntType(this.toPosition(savePos))
        is DecTypeContext -> return DecType(this.toPosition(savePos))
        else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
    }
}

fun BinOpContext.toAST(savePos: Boolean = false): Expression {
    when(this.operator.text) {
        "+" -> return AdditionExpr(this.left.toAST(savePos),
                                    this.right.toAST(savePos),
                                    this.toPosition(savePos))
        "-" -> return SubstractionExpr(this.left.toAST(savePos),
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