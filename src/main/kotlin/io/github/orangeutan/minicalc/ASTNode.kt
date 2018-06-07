package io.github.orangeutan.minicalc

import kotlin.reflect.full.*
import  kotlin.reflect.jvm.*

import java.lang.reflect.ParameterizedType
import java.util.IdentityHashMap

interface ASTNode {
    val position: Position?
    var parent: ASTNode?

    fun <T: ASTNode> findNearestAncestor(clazz: Class<T>): T? {
        if(this.parent != null) {
            return if(clazz.isInstance(this.parent)) this.parent as T
                   else this.parent!!.findNearestAncestor(clazz)
        }
        return null
    }

    /* Executes an operation on all AST Nodes */
    fun execOnAST(op: (ASTNode) -> Unit) {
        op(this)
        this.javaClass.kotlin.memberProperties.filter {
            !it.name.equals("parent") && !it.name.equals("position")
        }.forEach({ p ->
            val value = p.get(this) // Get the value of the member Property of this instance
            when(value) {
                is ASTNode -> value.execOnAST(op)
                is Collection<*> -> value.forEach({ if(it is ASTNode) it.execOnAST(op) })
            }
        })
    }

    /** Executes an operation on all AST Nodes for which the filter returns true */
    fun execOnAST(filter: (ASTNode) -> Boolean, op: (ASTNode) -> Unit) {
        execOnAST({ if(filter(it)) op(it) })
    }

    /** Executes an operation on all ASTNodes of a specific class */
    fun <T: ASTNode> execOnAST(clazz: Class<T>, op: (T) -> Unit) {
        execOnAST { if(clazz.isInstance(it)){ op(it as T) } }
    }

    fun isBefore(node: ASTNode): Boolean
        = position!!.start.isBefore(node.position!!.start)

    fun toMultilineStr(indent: String = ""): String {
        val sb = StringBuffer()
        sb.append("$indent${this.javaClass.simpleName} {\n")
    
        this.javaClass.kotlin.memberProperties.filter({ 
            !it.name.startsWith("component") && !it.name.equals("parent") && !it.name.equals("position")
        }).forEach({ 
            val retType = it.returnType.javaType
            if(retType is ParameterizedType && retType.rawType.equals(List::class.java)) {
                val paramType = retType.actualTypeArguments[0]
                if(paramType is Class<*> && ASTNode::class.java.isAssignableFrom(paramType)) {
                    sb.append("$indent${"    "}${it.name} = [\n")
                    (it.get(this) as List<out ASTNode>).forEach({
                        sb.append(it.toMultilineStr("$indent        "))
                    })
                    sb.append("$indent${"    "}]\n")
                }
            } else {
                val value = it.get(this)
                if(value is ASTNode) {
                    sb.append("$indent${"    "}${it.name} = [\n")
                    sb.append(value.toMultilineStr("$indent        "))
                    sb.append("$indent${"    "}${it.name} = ${it.get(this)}\n")
                }
            }
        })
        sb.append("$indent}\n")
        return sb.toString()
    }

    fun <T: ASTNode> collectByType(clazz: Class<T>): List<T> {
        val typeList = mutableListOf<T>()

        this.execOnAST(clazz) {
            typeList.add(it as T)
        }

        return typeList
    }
}

data class Point(val line: Int, val column: Int) {
    override fun toString() 
        = "Line $line, Column $column"

    /* 
        Measures the position of this Point in a String.
        This method converts line number and column to the coresponding index/position in the given String.
    */
    fun posIn(str: String, lineSeperator: Char = '\n'): Int {
        /* Split the String into lines without removing the line seperators */
        val lines = str.split("(?=" + lineSeperator +")");
        return lines.subList(0, this.line-1)
                    .foldRight(0, { it,acc -> it.length + acc }) + column  
    }

    fun isBefore(p: Point): Boolean 
        = this.line < p.line || (this.line == p.line && this.column < p.column)
}

data class Position(val start: Point, val end: Point) {
    constructor(startLine: Int, startCol: Int, endLine: Int, endCol: Int): 
        this(Point(startLine, startCol), Point(endLine, endCol))

    init {
        if(end.isBefore(start)) throw IllegalArgumentException("End Point can't be before start Point")
    }

    /* Extracts a substring from a String located at this Position inside that String */
    fun substrFrom(str: String): String
        = str.substring(start.posIn(str), end.posIn(str))

    /* Calculates the length of this Position in a String */
    fun length(str: String)
        = end.posIn(str) - start.posIn(str)
}