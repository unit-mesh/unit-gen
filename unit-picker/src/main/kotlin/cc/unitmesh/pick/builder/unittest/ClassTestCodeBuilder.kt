package cc.unitmesh.pick.builder.unittest

import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.core.unittest.TestCodeBuilder
import cc.unitmesh.pick.ext.checkNamingStyle
import cc.unitmesh.pick.ext.toSourceCode
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeDataStruct

class ClassTestCodeBuilder(private val context: JobContext) : TestCodeBuilder {
    /**
     * @param dataStruct is the test file
     * @param underTestFile is the file under test
     * @param relevantClasses are the classes relevant to the test file
     */
    override fun build(
        dataStruct: CodeDataStruct,
        underTestFile: CodeDataStruct,
        relevantClasses: List<CodeDataStruct>,
    ): List<BasicTestIns> {
        var generatedCode = dataStruct.toSourceCode()
        if (generatedCode.lines().size > context.insQualityThreshold.maxLineInCode) {
            return emptyList()
        }

        val namingStyle = dataStruct.checkNamingStyle()

        // remove all `import` statements
        generatedCode = generatedCode.lines().filter {
            !(it.startsWith("import") && it.endsWith(";"))
        }.joinToString("\n")

        return listOf(
            BasicTestIns(
                language = context.project.language,
                underTestCode = underTestFile.Content,
                generatedCode = generatedCode,
                coreFrameworks = context.project.coreFrameworks,
                testFrameworks = context.project.testFrameworks,
                testType = TestCodeBuilderType.CLASS_UNIT,
                specs = listOf(
                    "Test class should be named `${namingStyle}`."
                )
            )
        )
    }
}
