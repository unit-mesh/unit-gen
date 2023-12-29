package cc.unitmesh.pick.similar

import cc.unitmesh.core.intelli.SimilarChunkContext
import cc.unitmesh.core.intelli.SimilarChunker
import cc.unitmesh.pick.worker.job.InstructionFileJob
import java.util.HashMap

class TypeScriptSimilarChunker(fileTree: HashMap<String, InstructionFileJob>): SimilarChunker() {
    override fun calculate(text: String, canonicalName: String): SimilarChunkContext {
        TODO("Not yet implemented")
    }
}
