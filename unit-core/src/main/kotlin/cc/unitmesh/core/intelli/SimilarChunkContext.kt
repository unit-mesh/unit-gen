package cc.unitmesh.core.intelli

import cc.unitmesh.core.base.LLMCodeContext

class SimilarChunkContext(val language: String, private val paths: List<String>?, private val chunks: List<String>?) : LLMCodeContext {
    override fun format(): String {
        val commentPrefix = CommentService.getInstance().lineComment(language)

        if (paths == null || chunks == null) return ""

        val filteredPairs = paths.zip(chunks).filter { it.second.isNotEmpty() }

        val queryBuilder = StringBuilder()
        for ((path, chunk) in filteredPairs) {
            val commentedCode = commentCode(chunk, commentPrefix)
            queryBuilder.append("$commentPrefix Compare this snippet from $path\n")
            queryBuilder.append(commentedCode).append("\n")
        }

        return queryBuilder.toString().trim()
    }

    private fun commentCode(code: String, commentSymbol: String?): String {
        if (commentSymbol == null) return code

        return code.split("\n").joinToString("\n") { "$commentSymbol $it" }
    }
}
