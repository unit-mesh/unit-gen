package cc.unitmesh.runner.cli;

import com.charleskorn.kaml.Yaml
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class UnitEvalConfigTest  {
    @Test
    fun shouldSerialize() {
        val config = """
projects:
  # Functional Programming
  - repository: https://github.com/fpinjava/fpinjava
    branch: master
    language: java
"""
        val unitEvalConfig = Yaml.default.decodeFromString(deserializer = UnitEvalConfig.serializer(), config)
        unitEvalConfig.projects.size shouldBe 1

        val sourceCode = unitEvalConfig.projects[0]
        sourceCode.repository shouldBe "https://github.com/fpinjava/fpinjava"

        unitEvalConfig.instructionConfig.mergeInput shouldBe false
    }

    @Test
    fun shouldSerializeWithInstructionConfig() {
        val config = """
projects:
  # Functional Programming
  - repository: https://github.com/fpinjava/fpinjava
    branch: master
    language: java
    
instructionConfig:
    mergeInput: true
    """
        val unitEvalConfig = Yaml.default.decodeFromString(deserializer = UnitEvalConfig.serializer(), config)
        unitEvalConfig.projects.size shouldBe 1

        unitEvalConfig.instructionConfig.mergeInput shouldBe true
    }
}