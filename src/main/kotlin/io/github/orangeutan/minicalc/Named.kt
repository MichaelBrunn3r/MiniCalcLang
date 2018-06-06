package io.github.orangeutan.minicalc;

interface Named {
    val name: String
}

data class ReferenceByName<N: Named>(var name: String, var referred: N? = null) {
    override fun toString(): String
        = "Ref($name)[${if(referred == null) "Unsolved" else "Solved"}]"

    fun tryToResolve(candidates: List<N>): Boolean {
        val res: N? = candidates.find { it.name == this.name }
        this.referred = res
        return res != null
    }
}