package cc.unitmesh.pick.related

import cc.unitmesh.core.intelli.SimilarChunkContext
import cc.unitmesh.core.intelli.SimilarChunksWithPaths
import java.io.File

class JavaSimilarChunks : SimilarChunksWithPaths() {
    override fun similarChunksWithPaths(text: String): SimilarChunkContext {
        TODO("Not yet implemented")
    }

    override fun extractChunks(mostRecentFiles: List<File>): List<List<String>> {
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