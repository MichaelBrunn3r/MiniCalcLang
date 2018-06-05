package io.github.orangeutan.minicalc

// MiniCalc main entities

data class MiniCalcFile(val statements: List<Statement>, 
                         override val position: Position? = null): ASTNode

interface Statement: ASTNode

interface Expression: ASTNode

interface Type: ASTNode

// Types

data class IntType(override val position: Position? = null): Type

data class DecType(override val position: Position? = null): Type

data class StrType(override val position: Position? = null): Type

/* Binary Expression */

interface BinaryExpression: Expression {
    val left: Expression
    val right: Expression
}

data class AdditionExpr(override val left: Expression, override val right: Expression, 
                         override val position: Position? = null): BinaryExpression


data class SubstractionExpr(override val left: Expression, override val right: Expression, 
                             override val position: Position? = null): BinaryExpression


data class MultiplicationExpr(override val left: Expression, override val right: Expression, 
                               override val position: Position? = null): BinaryExpression


data class DivisionExpr(override val left: Expression, override val right: Expression, 
                         override val position: Position? = null): BinaryExpression


/* Expressions */

data class UnaryMinusExpr(val value: Expression, 
                           override val position: Position? = null): Expression

data class TypeConversion(val value: Expression, val targetType: Type, 
                           override val position: Position? = null): Expression


data class NamedValRef(val varName: ReferenceByName<NamedValDeclaration>,
                               override val position: Position? = null): Expression


data class IntLit(val value: String, override val position: Position? = null): Expression


data class DecLit(val value: String, override val position: Position? = null): Expression

data class StrLit(val parts: List<StrLitContent>, 
                      override val position: Position? = null): Expression

/* Sring Literal Expressions */

interface StrLitContent: ASTNode

data class StrLitConstContent(val content: String, 
                               override val position: Position? = null): StrLitContent

data class StrLitInterpContent(val expression: Expression,
                              override val position: Position? = null): StrLitContent


/* Statements */

interface NamedValDeclaration: Statement, Named

data class VarDeclaration(override val name: String, val value: Expression,
                           override val position: Position? = null): NamedValDeclaration

data class InputDeclaration(override val name: String, val type: Type,
                             override val position: Position? = null): NamedValDeclaration


data class Assignment(val varDecl: ReferenceByName<VarDeclaration>, val value: Expression,
                       override val position: Position? = null): Statement

data class Print(val value: Expression, override val position: Position? = null): Statement