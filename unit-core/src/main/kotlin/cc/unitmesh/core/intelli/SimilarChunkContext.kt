package cc.unitmesh.core.intelli

import cc.unitmesh.core.base.LLMCodeContext

class SimilarChunkContext(val language: String, val paths: List<String>, val chunks: List<String>) : LLMCodeContext {
    override fun format(): String {
        TODO("Not yet implemented")
    }
}
