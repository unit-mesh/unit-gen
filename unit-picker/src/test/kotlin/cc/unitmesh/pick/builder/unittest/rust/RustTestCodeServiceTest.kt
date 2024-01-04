package cc.unitmesh.pick.builder.unittest.rust;

import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.pick.option.InsOutputConfig
import cc.unitmesh.pick.threshold.InsQualityThreshold
import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.pick.worker.job.JobContext
import cc.unitmesh.quality.CodeQualityType
import chapi.ast.rustast.RustAnalyser
import org.archguard.scanner.analyser.count.FileJob
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class RustTestCodeServiceTest {

    @Test
    fun shouldReturnFalseWhenNotApplicableToCodeDataStruct() {
        val testCode = """
           use std::sync::Arc;

           pub use embedding::Embedding;
           pub use embedding::Semantic;
           pub use embedding::semantic::SemanticError;

           pub fn init_semantic(model: Vec<u8>, tokenizer_data: Vec<u8>) -> Result<Arc<Semantic>, SemanticError> {
                let result = Semantic::init_semantic(model, tokenizer_data)?;
                Ok(Arc::new(result))
           }
           
           #[cfg(test)]
           mod tests {
               use super::*;
           
               #[test]
               #[cfg_attr(feature = "ci", ignore)]
               fn test_init_semantic() {
                   let model = std::fs::read("../model/model.onnx").unwrap();
                   let tokenizer_data = std::fs::read("../model/tokenizer.json").unwrap();
           
                   let semantic = init_semantic(model, tokenizer_data).unwrap();
                   let embedding = semantic.embed("hello world").unwrap();
                   assert_eq!(embedding.len(), 128);
               }
           }
       """.trimIndent()

        val container = RustAnalyser().analysis(testCode, "lib.rs")
        val testFileJob = InstructionFileJob(
            FileJob(
            ),
            codeLines = testCode.lines(),
            code = testCode,
            container = container
        )
        val context = JobContext(
            job = testFileJob,
            qualityTypes = listOf(CodeQualityType.JavaController),
            fileTree = hashMapOf(
                "lib" to testFileJob,
            ),
            insOutputConfig = InsOutputConfig(),
            completionBuilderTypes = listOf(CompletionBuilderType.TEST_CODE_GEN),
            maxTypedCompletionSize = 3,
            insQualityThreshold = InsQualityThreshold()
        )
        val rustTestCodeService = RustTestCodeService(context)
        val build = rustTestCodeService.build(container)

        assertEquals(0, build.size)
    }

}
