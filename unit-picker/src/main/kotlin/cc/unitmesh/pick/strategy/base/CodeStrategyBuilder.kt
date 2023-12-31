package cc.unitmesh.pick.strategy.base

import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.quality.CodeQualityType
import cc.unitmesh.quality.QualityAnalyser
import chapi.domain.core.CodeDataStruct

/**
 * The InstructionBuilder interface is used to build instructions from template data. It is a generic interface that
 * takes a type parameter <T> which represents the instruction data.
 *
 * The convert() method is used to convert the template data into the instruction data <T>. The implementation of this
 * method will vary depending on the specific builder used.
 *
 * The build() method is used to build the instruction from the instruction data <T> and returns a list of instructions.
 * The implementation of this method will also vary depending on the specific builder used.
 *
 * The companion object provides a build() function that takes a list of InstructionType objects, a fileTree
 * (HashMap<String, InstructionJob>), and an InstructionJob. It creates an InstructionContext using the provided
 * InstructionJob and fileTree. It then iterates over the list of InstructionType objects and calls the builder()
 * function on each type, passing in the InstructionContext. The build() function returns a collection of instructions
 * obtained by flattening the list of instructions returned by each builder.
 *
 * Note: The InstructionBuilder interface does not provide documentation for its methods as per the given requirements.
 */
interface CodeStrategyBuilder {
    /**
     * In AutoDev or ChocolateFactory, we use Apache Velocity to generate instruction from template. In different cases,
     * the template is different, so we use different builders to build instruction data, aka <T>.
     *
     * For example, here is the Code Completion template:
     * ```vm
     * Complete ${context.lang} code, return rest code, no explaining
     *
     * ```uml
     * {context.relatedCode}
     * ```
     *
     * Code:
     * ```${context.lang}
     * ${context.beforeCursor}
     * ```
     *
     * ```
     *
     * In this case, the instruction data <T> should be included: `lang`, `relatedCode`, `beforeCursor`.
     */
    fun build(): List<TypedIns>

    /**
     * Checks if a given code node passes the specified code quality checks.
     *
     * @param node The code node to be checked.
     * @param types The list of [CodeQualityType] to be checked against.
     * @return `true` if the code node passes all the specified code quality checks, `false` otherwise.
     */
    fun checkIssue(node: CodeDataStruct, types: List<CodeQualityType>): Boolean {
        return QualityAnalyser.create(types).map { analyser ->
            analyser.analysis(listOf(node))
        }.flatten().isEmpty()
    }
}


