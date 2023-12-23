package cc.unitmesh.core.completion

enum class CompletionBuilderType {
    /**
     * generate code after cursor, like text after `Blog blog = `, will be `new Blog();`
     */
    INLINE_COMPLETION,

    /**
     * generate the rest of code of correct block, like function inner code
     */
    IN_BLOCK_COMPLETION,

    /**
     * generate code after block, like multiple functions
     */
    AFTER_BLOCK_COMPLETION,

    /**
     * generate full file code, like test code, class code, api code
     */
    TEST_CODE_GEN,
}