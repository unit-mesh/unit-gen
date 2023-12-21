package cc.unitmesh.pick.threshold

import kotlinx.serialization.Serializable

@Serializable
data class QualityThreshold(
    val complexity: Long = MAX_COMPLEXITY,
    val fileSize: Long = MAX_FILE_SIZE,
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
     * Our token length is 1024, so we can use 40 * 2048 / 512 = 160
     */
    val maxLineInCode: Int = MAX_LINE_IN_CODE,
) {
    companion object {
        const val MAX_COMPLEXITY: Long = 100
        const val MAX_FILE_SIZE: Long = 1024 * 64
        const val MAX_LINE_IN_CODE: Int = 160
        const val MAX_CHAR_IN_CODE: Int = 1500
        const val MAX_RELATED_CODE_LINE: Int = 30
    }
}