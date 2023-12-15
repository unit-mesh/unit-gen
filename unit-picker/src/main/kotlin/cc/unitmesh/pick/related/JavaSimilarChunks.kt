package cc.unitmesh.pick.related

import cc.unitmesh.core.intelli.SimilarChunkContext
import cc.unitmesh.core.intelli.SimilarChunksWithPaths

class JavaSimilarChunks : SimilarChunksWithPaths() {
    override fun calculate(text: String): SimilarChunkContext {
        TODO("Not yet implemented")
    }

    fun chunkedCode(code: String): List<String> {
        return code
            .split("\n", limit = snippetLength)
            .filter {
                val trim = it.trim()
                !(trim.startsWith("import ") || trim.startsWith("package "))
            }
            .chunked(snippetLength)
            .flatten()
    }
}