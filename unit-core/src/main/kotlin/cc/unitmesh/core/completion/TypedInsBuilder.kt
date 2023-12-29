package cc.unitmesh.core.completion

import chapi.domain.core.CodeContainer
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction

/**
 * TypedInsBuilder is an interface that allows for the construction of different types of completions based on the provided code.
 *
 * To use this interface, you need to implement it and override the build methods according to the desired completion type.
 * The build methods return a list of TypedIns objects, which represent the generated completions.
 *
 * This interface provides three build methods:
 *
 * 1. build(container: CodeContainer): List<TypedIns>
 *    - This method builds a list of TypedIns objects based on the provided CodeContainer.
 *    - It is suitable for file-level analysis, such as generating completions for documentation.
 *    - The CodeContainer parameter contains the data structures and functions to be analyzed.
 *    - The method returns a list of TypedIns objects representing the extracted comments from the CodeContainer.
 *
 * 2. build(dataStruct: CodeDataStruct): List<TypedIns>
 *    - This method builds a list of TypedIns objects based on the given CodeDataStruct.
 *    - It is suitable for generating code related to the entire class.
 *    - The CodeDataStruct parameter represents the class to be analyzed.
 *    - The method returns a list of TypedIns objects generated based on the CodeDataStruct.
 *
 * 3. build(function: CodeFunction): List<CodeCompletionIns>
 *    - This method builds a list of code completion instructions based on a given function.
 *    - It is suitable for generating code completions for inline and interline completion.
 *    - The CodeFunction parameter represents the function to be analyzed.
 *    - The method returns a list of CodeCompletionIns objects representing the code completion instructions.
 *
 * Note: The default implementation of these build methods returns an empty list.
 *
 * Example usage:
 *
 * ```kotlin
 * val builder = MyTypedInsBuilder()
 * val container = CodeContainer(...)
 * val completions = builder.build(container)
 * ```
 */
interface TypedInsBuilder {
    /**
     * Builds a list of TypedIns objects based on the provided CodeContainer.
     * This method is suitable for file-level analysis, such as:
     *
     * - [CompletionBuilderType.DOCUMENTATION]
     *
     * @param container The CodeContainer containing the data structures and functions to be analyzed.
     * @return A list of TypedIns objects representing the comments extracted from the CodeContainer.
     */
    fun build(container: CodeContainer): List<TypedIns> {
        return listOf()
    }

    /**
     * Builds a list of TypedIns objects based on the given CodeDataStruct.
     * This method is suitable for generating code related to the entire class, such as:
     *
     * - [CompletionBuilderType.TEST_CODE_GEN]
     *
     * @param dataStruct The CodeDataStruct object representing the class to be analyzed.
     * @return A list of TypedIns objects generated based on the CodeDataStruct.
     */
    fun build(dataStruct: CodeDataStruct): List<TypedIns> {
        return listOf()
    }

    /**
     * Builds a list of code completion instructions based on a given function.
     *
     * @param function The CodeFunction object representing the function to be analyzed.
     * @return A list of CodeCompletionIns objects representing the code completion instructions.
     *
     * This method is suitable for generating code completions for inline and interline completion, such as :
     *
     *  - [CompletionBuilderType.INLINE_COMPLETION]
     *  - [CompletionBuilderType.IN_BLOCK_COMPLETION]
     *  - [CompletionBuilderType.AFTER_BLOCK_COMPLETION]
     */
    fun build(function: CodeFunction): List<CodeCompletionIns> {
        return listOf()
    }
}


