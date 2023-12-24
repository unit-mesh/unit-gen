package cc.unitmesh.pick.option

import cc.unitmesh.pick.strategy.BizCodeContextStrategy
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.quality.CodeQualityType
import cc.unitmesh.quality.badsmell.BadsmellAnalyser
import cc.unitmesh.quality.extension.JavaControllerAnalyser
import cc.unitmesh.quality.extension.JavaRepositoryAnalyser
import cc.unitmesh.quality.extension.JavaServiceAnalyser
import cc.unitmesh.quality.testbadsmell.TestBadsmellAnalyser
import kotlinx.serialization.Serializable
import java.io.File

const val MAX_COMPLETION_EACH_FILE = 3

/**
 * Represents an option for picking a code repository.
 *
 * This class provides options for configuring the parameters used in the process of picking a code repository.
 * It encapsulates the necessary information such as the git URL, branch, programming language, base directory,
 * code context strategies, completion types, code quality types, configuration for the builder, and other parameters.
 *
 * @property url The git URL of the repository.
 * @property branch The branch of the repository. Default value is "master".
 * @property language The programming language of the code in the repository. Default value is "java".
 * @property baseDir The base directory where the datasets are stored. Default value is "datasets".
 * @property codeContextStrategies The strategies to determine the code context. Default value is [BizCodeContextStrategy.RELATED_CODE].
 *        Possible values are:
 *        - [CodeContextStrategy.SIMILAR_CHUNKS]: Determines the code context based on similar code chunks.
 *        - [CodeContextStrategy.RELATED_CODE]: Determines the code context based on related code.
 * @property completionTypes The types of completion builders to use. Default values are [CompletionBuilderType.AFTER_BLOCK_COMPLETION],
 *        [CompletionBuilderType.IN_BLOCK_COMPLETION], [CompletionBuilderType.INLINE_COMPLETION].
 * @property codeQualityTypes The code quality types to analyze. Default value is an empty list. Possible values are:
 *        - [CodeQualityType.BadSmell]: Analyzes bad smells in the code using [BadsmellAnalyser].
 *        - [CodeQualityType.TestBadSmell]: Analyzes bad smells in the test code using [TestBadsmellAnalyser].
 *        - [CodeQualityType.JavaController]: Analyzes a Java controller class using [JavaControllerAnalyser].
 *        - [CodeQualityType.JavaRepository]: Analyzes a Java repository class using [JavaRepositoryAnalyser].
 *        - [CodeQualityType.JavaService]: Analyzes a Java service class using [JavaServiceAnalyser].
 * @property insOutputConfig The configuration for the builder.
 * @property maxCompletionEachFile The maximum number of completions allowed in each file.
 * @property gitDepth The depth of the git history to analyze.
 * @property completionTypeSize The default instruction size for each completion type.
 * @property maxCharInCode The maximum number of characters allowed in the code.
 * @property maxLineInCode The maximum number of lines allowed in the code.
 */
@Serializable
data class InsPickerOption(
    /* The url of the repo should be git url */
    val url: String,
    /* The branch of the repo */
    val branch: String = "master",
    val language: String = "java",
    val baseDir: String = "datasets",
    /**
     * The [BizCodeContextStrategy], suggest to be one of:.
     *
     * - [BizCodeContextStrategy.SIMILAR_CHUNKS]
     * - [BizCodeContextStrategy.RELATED_CODE]
     *
     */
    val codeContextStrategies: List<BizCodeContextStrategy> = listOf(
        BizCodeContextStrategy.RELATED_CODE,
//        BizCodeContextStrategy.SIMILAR_CHUNKS,
    ),
    /**
     * The [CompletionBuilderType], which will according you IDE strategy to generate the type.
     */
    val completionTypes: List<CompletionBuilderType> = listOf(
//        CompletionBuilderType.AFTER_BLOCK_COMPLETION,
//        CompletionBuilderType.IN_BLOCK_COMPLETION,
//        CompletionBuilderType.INLINE_COMPLETION,
        CompletionBuilderType.TEST_CODE_GEN,
    ),
    /**
     * The [CodeQualityType], will be like a tree to hold the item.
     * Two basic strategies:
     *
     * - [CodeQualityType.BadSmell]  -> [BadsmellAnalyser],
     * - [CodeQualityType.TestBadSmell] -> [TestBadsmellAnalyser]
     *
     * For MVC case:
     *
     * - [CodeQualityType.JavaController] -> [JavaControllerAnalyser]
     * - [CodeQualityType.JavaRepository] -> [JavaRepositoryAnalyser]
     * - [CodeQualityType.JavaService] -> [JavaServiceAnalyser]
     *
     */
    val codeQualityTypes: List<CodeQualityType> = listOf(),
    val insOutputConfig: InsOutputConfig = InsOutputConfig(),
    val maxCompletionEachFile: Int = MAX_COMPLETION_EACH_FILE,
    val gitDepth: Int = 1,
    /**
     * the default instruction size for each completion type
     */
    val completionTypeSize: Int = 1000,
    /**
     * https://docs.sweep.dev/blogs/chunking-2m-files
     * This is because the average token to a character ratio for code is ~1:5(300 tokens), and embedding models are
     *  capped at 512 tokens. Further, 1500 characters correspond approximately to 40 lines, roughly equivalent to a
     *  small to medium-sized function or class.
     *
     * Our token length is 2048, so we can use 1500 * 1024 / 512 = 3000
     */
    val maxCharInCode: Int = InsQualityThreshold.MAX_CHAR_IN_CODE,
    /**
     * Our token length is 2048, so we can use 40 * 2048 / 512 = 160, but java has a lot of new lines, so we double it
     */
    val maxLineInCode: Int = InsQualityThreshold.MAX_LINE_IN_CODE,
) {
    fun pureDataFileName(): String {
        return baseDir + File.separator + repoFileName() + ".jsonl"
    }

    fun repoFileName() = "${encodeFileName(url)}_${encodeFileName(branch)}_${language}.jsonl"

    // for / \ : * ? " < > |, which is not allowed in file name
    fun encodeFileName(string: String): String {
        return string.replace("/", "_")
            .replace("\\", "_")
            .replace(":", "_")
            .replace("*", "_")
            .replace("?", "_")
            .replace("\"", "_")
            .replace("<", "_")
            .replace(">", "_")
            .replace("|", "_")
    }
}
