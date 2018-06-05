package io.github.orangeutan.minicalc;

interface Named {
    val name: String
}

data class ReferenceByName<N>(val name: String, var referred: N? = null) where N: Named {
    override fun toString(): String
        = "Ref($name)[${if(referred == null) "Unsolved" else "Solved"}]"

    fun tryToResolve(candidates: List<N>): Boolean {
        val res = candidates.find({it.name == this.name})
        this.referred = res
        return res != null
    }
}