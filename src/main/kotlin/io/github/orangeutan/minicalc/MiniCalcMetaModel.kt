package io.github.orangeutan.minicalc

// MiniCalc main entities

data class MiniCalcFile(val statements: List<Statement>,
                        override val position: Position? = null): ASTNode {
    override var parent: ASTNode? = null
    init {
        this.statements.forEach{ it.parent = this}
    }

    fun resolveSymbols() {
        // resolve reference to the closest thing before
        this.execOnAST(clazz = IDRef::class.java) {
            val statement = it.findNearestAncestor(Statement::class.java)!!
            val namedValDeclarations = this.statements.subList(0, this.statements.indexOf(statement))
                                                                                .filterIsInstance<NamedValDeclaration>()
            it.varName.tryToResolve(namedValDeclarations.reversed())
        }

        // consider assignments
        this.execOnAST (clazz= Assignment::class.java) {
            val namedValDeclarations = this.statements.subList(0, this.statements.indexOf(it))
                                                                        .filterIsInstance<VarDeclaration>()
            it.varDecl.tryToResolve(namedValDeclarations.reversed())
        }
    }
}

interface Statement: ASTNode

interface Expression: ASTNode

interface Type: ASTNode

// Types

data class IntType(override val position: Position? = null): Type {
    override var parent: ASTNode? = null
}

data class DecType(override val position: Position? = null): Type {
    override var parent: ASTNode? = null
}

data class StrType(override val position: Position? = null): Type {
    override var parent: ASTNode? = null
}

/* Binary Expression */

interface BinaryExpression: Expression {
    val left: Expression
    val right: Expression
}

data class AdditionExpr(override val left: Expression, override val right: Expression,
                        override val position: Position? = null): BinaryExpression {
    override var parent: ASTNode? = null
    init {
        this.left.parent = this
        this.right.parent = this
    }
}


data class SubtractionExpr(override val left: Expression, override val right: Expression,
                           override val position: Position? = null): BinaryExpression {
    override var parent: ASTNode? = null
    init {
        this.left.parent = this
        this.right.parent = this
    }
}


data class MultiplicationExpr(override val left: Expression, override val right: Expression, 
                               override val position: Position? = null): BinaryExpression {
    override var parent: ASTNode? = null
    init {
        this.left.parent = this
        this.right.parent = this
    }
}


data class DivisionExpr(override val left: Expression, override val right: Expression, 
                         override val position: Position? = null): BinaryExpression {
    override var parent: ASTNode? = null
    init {
        this.left.parent = this
        this.right.parent = this
    }
}


/* Expressions */

data class UnaryMinusExpr(val value: Expression, 
                           override val position: Position? = null): Expression {
    override var parent: ASTNode? = null
    init {
        this.value.parent = this
    }
}

data class TypeConversion(val value: Expression, val targetType: Type, 
                           override val position: Position? = null): Expression {
    override var parent: ASTNode? = null
    init {
        this.value.parent = this
    }
}


data class IDRef(val varName: ReferenceByName<NamedValDeclaration>,
                               override val position: Position? = null): Expression {
    override var parent: ASTNode? = null
}


data class IntLit(val value: String, override val position: Position? = null): Expression {
    override var parent: ASTNode? = null
}


data class DecLit(val value: String, override val position: Position? = null): Expression {
    override var parent: ASTNode? = null
}

data class StrLit(val parts: List<StrLitContent>, 
                      override val position: Position? = null): Expression {
    override var parent: ASTNode? = null
}

/* Sring Literal Expressions */

interface StrLitContent: ASTNode

data class StrLitConstContent(val content: String, 
                               override val position: Position? = null): StrLitContent {
    override var parent: ASTNode? = null
}

data class StrLitInterpContent(val expression: Expression,
                              override val position: Position? = null): StrLitContent {
    override var parent: ASTNode? = null
    init {
        this.expression.parent = this
    }
}


/* Statements */

interface NamedValDeclaration: Statement, Named

data class VarDeclaration(override var name: String, val value: Expression,
                           override val position: Position? = null): NamedValDeclaration {
    override var parent: ASTNode? = null
    init {
        this.value.parent = this
    }
}

data class InputDeclaration(override var name: String, val type: Type,
                             override val position: Position? = null): NamedValDeclaration {
    override var parent: ASTNode? = null
    init {
        this.type.parent = this
    }
}


data class Assignment(val varDecl: ReferenceByName<VarDeclaration>, val value: Expression,
                       override val position: Position? = null): Statement {
    override var parent: ASTNode? = null
    init {
        this.value.parent = this
    }
}

data class Print(val value: Expression, override val position: Position? = null): Statement {
    override var parent: ASTNode? = null
    init {
        this.value.parent = this
    }
}