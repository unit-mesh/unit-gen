package cc.unitmesh.pick.picker

enum class ToolingPromptType {
    /**
     * like GitHub Copilot, use similar chunk of code to complete
     */
    INLINE_CODE_COMPLETION,

    /**
     * like GitHub Copilot, use similar chunk of code to complete
     */
    IN_BLOCK_CODE_COMPLETION,

    /**
     * like GitHub Copilot, use similar chunk of code to complete
     */
    AFTER_BLOCK_CODE_COMPLETION,

    /**
     * like AutoDev, use Static Program Analysis for code based on imports
     */
    RELATED_CODE_COMPLETION,
    CODE_DIFF,
    REFACTOR,
}