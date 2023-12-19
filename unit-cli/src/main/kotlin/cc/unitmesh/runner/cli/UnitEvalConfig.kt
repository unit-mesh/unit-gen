package cc.unitmesh.runner.cli

import kotlinx.serialization.Serializable

@Serializable
data class UnitEvalConfig(
    val projects: List<SourceCode>,
    val instructionConfig: InstructionConfig = InstructionConfig(),
)

@Serializable
data class InstructionConfig(
    /**
     * the Default instruction will be like:
     * ```json
     * {
     *   "instruction": "some command",
     *   "input": "some-input",
     *   "output": "xxx"
     * }
     *
     * if mergeInstructionInput is true, the instruction will be like:
     * ```json
     * {
     *  "instruction": "some command\nsome-input",
     *  "input": "",
     *  "output": "xxx"
     * }
     * ```
     */
    val mergeInput: Boolean = false,
)

@Serializable
data class SourceCode(
    val repository: String,
    val branch: String,
    val language: String,
)