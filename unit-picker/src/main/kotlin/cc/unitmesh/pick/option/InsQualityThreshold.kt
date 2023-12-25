package cc.unitmesh.pick.option

import cc.unitmesh.quality.badsmell.BsThresholds
import kotlinx.serialization.Serializable

@Serializable
data class InsQualityThreshold(
    val complexity: Int = MAX_COMPLEXITY,
    val fileSize: Int = MAX_FILE_SIZE,
    /**
     * https://docs.sweep.dev/blogs/chunking-2m-files
     * This is because the average token to a character ratio for code is ~1:5(300 tokens), and embedding models are
     *  capped at 512 tokens. Further, 1500 characters correspond approximately to 40 lines, roughly equivalent to a
     *  small to medium-sized function or class.
     *
     * Our token length is 1024, so we can use 1500 * 1024 / 512 = 1500
     */
    val maxCharInCode: Int = MAX_CHAR_IN_CODE,
    /**
     * Our token length is 1024, so we can use 40 * 2048 / 512 = 320
     */
    val maxLineInCode: Int = MAX_LINE_IN_CODE,
    val badsmellThreshold: Map<String, Int> = BsThresholds().toThresholds(),
    val maxTokenLength: Int = MAX_TOKEN_LENGTH,
) {
    companion object {
        const val MAX_TOKEN_LENGTH: Int = 2048
        const val MAX_COMPLEXITY: Int = 1000
        const val MAX_FILE_SIZE: Int = 1024 * 64
        const val MAX_LINE_IN_CODE: Int = 320
        const val MAX_CHAR_IN_CODE: Int = 1500
        const val MAX_RELATED_CODE_LINE: Int = 30
    }
}