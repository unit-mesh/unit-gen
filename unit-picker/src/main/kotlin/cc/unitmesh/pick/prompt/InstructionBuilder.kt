package cc.unitmesh.pick.prompt

import cc.unitmesh.quality.CodeQualityType
import cc.unitmesh.quality.QualityAnalyser
import chapi.domain.core.CodeDataStruct

interface InstructionBuilder<T> {
    fun build(): List<T>

    /**
     * Build instruction from data <T>, and return a list of instructions.
     */
    fun unique(list: List<T>): List<Instruction>

    fun hasIssue(node: CodeDataStruct, types: List<CodeQualityType>): Boolean {
        return QualityAnalyser.create(types).map { analyser ->
            analyser.analysis(listOf(node))
        }.flatten().isEmpty()
    }
}


