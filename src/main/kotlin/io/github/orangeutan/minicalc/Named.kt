package io.github.orangeutan.minicalc;

interface Named {
    val name: String
}

data class ReferenceByName<N: Named>(var name: String, var referred: N? = null) {

    var isResolved = false

    init {
        if(referred != null) this.isResolved = true
    }

    override fun toString(): String
        = "Ref($name)[${if(this.isResolved) "Solved" else "Unsolved"}]"

    fun tryToResolve(candidates: List<N>): Boolean {
        val res: N? = candidates.find { it.name == this.name }
        this.referred = res
        this.isResolved = res != null
        return this.isResolved
    }
}