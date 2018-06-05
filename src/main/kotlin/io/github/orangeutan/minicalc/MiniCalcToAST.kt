package io.github.orangeutan.minicalc

import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.ParserRuleContext

import io.github.orangeutan.minicalc.MiniCalcParser.*

fun Token.startPoint() = Point(this.line, this.charPositionInLine)

fun Token.endPoint() = Point(this.line, this.charPositionInLine)

fun ParserRuleContext.toPosition(considerPosition: Boolean): Position? {
    if (considerPosition) {
        return Position(this.start.startPoint(), this.stop.endPoint())
    } else return null
}

fun MiniCalcFileContext.toAST(considerPosition: Boolean = false): MiniCalcFile
    = MiniCalcFile(
        this.line().map({ it.statement().toAST(considerPosition) }),
        this.toPosition(considerPosition))

fun StatementContext.toAST(considerPosition: Boolean = false): Statement {
    when(this) {
        is VarDeclarationStatementContext -> 
            return VarDeclaration(
                       this.varDeclaration().assignment().ID().text,
                       this.varDeclaration().assignment().expression().toAST(considerPosition),
                       this.toPosition(considerPosition))
        is AssignmentStatementContext -> 
            return Assignment(ReferenceByName(
                       this.assignment().ID().text),
                       this.assignment().expression().toAST(considerPosition),
                       this.toPosition(considerPosition))
        is PrintStatementContext -> 
            return Print(
                       this.print().expression().toAST(considerPosition),
                       this.toPosition(considerPosition))
        is InputDeclarationStatementContext -> 
            return InputDeclaration(
                       this.inputDeclaration().ID().text,
                       this.inputDeclaration().type().toAST(considerPosition),
                       this.toPosition(considerPosition))
        else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
    }
}

fun ExpressionContext.toAST(considerPosition: Boolean = false): Expression {
    when(this) {
        is BinOpContext -> return this.toAST(considerPosition)
        is IntLitContext -> return IntLit(this.text, this.toPosition(considerPosition))
        is DecLitContext -> return DecLit(this.text, this.toPosition(considerPosition))
        is StrLitContext -> 
            return StrLit(this.parts.map({ it.toAST(considerPosition) }),
                              this.toPosition(considerPosition))
        is ParenExprContext -> return this.expression().toAST(considerPosition)
        is IDRefContext -> return IDRef(ReferenceByName(this.text),
                                                       this.toPosition(considerPosition))
        is TypeConversionContext -> 
            return TypeConversion(this.expression().toAST(considerPosition),
                                   this.targetType.toAST(considerPosition),
                                   this.toPosition(considerPosition))
        else -> throw UnsupportedOperationException(this.javaClass.canonicalName)    
    }
}

fun StringLiteralContentContext.toAST(considerPosition: Boolean = false): StrLitContent {
    when(this) {
        is StrLitConstContentContext -> return StrLitConstContent(this.STR_CONTENT().text,
                                                                   this.toPosition(considerPosition))

        is StrLitInterpContentContext -> 
            return StrLitInterpContent(this.expression().toAST(considerPosition),
                                        this.toPosition(considerPosition))
        else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
    }
}

fun TypeContext.toAST(considerPosition: Boolean = false): Type {
    when(this) {
        is IntTypeContext -> return IntType(this.toPosition(considerPosition))
        is DecTypeContext -> return DecType(this.toPosition(considerPosition))
        else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
    }
}

fun BinOpContext.toAST(considerPosition: Boolean = false): Expression {
    when(this.operator.text) {
        "+" -> return AdditionExpr(this.left.toAST(considerPosition),
                                    this.right.toAST(considerPosition),
                                    this.toPosition(considerPosition))
        "-" -> return SubstractionExpr(this.left.toAST(considerPosition),
                                        this.right.toAST(considerPosition),
                                        this.toPosition(considerPosition))
        "*" -> return MultiplicationExpr(this.left.toAST(considerPosition),
                                          this.right.toAST(considerPosition),
                                          this.toPosition(considerPosition))
        "/" -> return DivisionExpr(this.left.toAST(considerPosition),
                                    this.right.toAST(considerPosition),
                                    this.toPosition(considerPosition))
        else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
    }
}