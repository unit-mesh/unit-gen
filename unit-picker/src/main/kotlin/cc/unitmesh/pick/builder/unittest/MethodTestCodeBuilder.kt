package cc.unitmesh.pick.builder.unittest

import cc.unitmesh.core.unittest.TestCodeBuilder
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.pick.ext.checkNamingStyle
import cc.unitmesh.pick.ext.toSourceCode
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeDataStruct

class MethodTestCodeBuilder(private val context: JobContext) : TestCodeBuilder {
    override fun build(
        dataStruct: CodeDataStruct,
        underTestFile: CodeDataStruct,
        relevantClasses: List<CodeDataStruct>,
    ): List<BasicTestIns> {
        val generatedCode = dataStruct.toSourceCode()
        if (generatedCode.lines().size > context.insQualityThreshold.maxLineInCode) {
            return emptyList()
        }

        val namingStyle = dataStruct.checkNamingStyle()
        // test canonicalName map BasicTestIns
        val results: HashMap<String, List<BasicTestIns>> = hashMapOf()

        val underTestFunctionMap = underTestFile.Functions.map {
            val canonicalName = underTestFile.Package + "." + underTestFile.NodeName + ":" + it.Name
            canonicalName to it.Content
        }.toMap()

        // analysis test code, and find original function content, put to results
        dataStruct.Functions.map { function ->
            function.FunctionCalls.map {
                if (it.NodeName == underTestFile.NodeName) {
                    val canonicalName = it.Package + "." + it.NodeName + ":" + it.FunctionName
                    val originalContent = underTestFunctionMap[canonicalName]

                    if (originalContent != null) {
                        val testIns = BasicTestIns(
                            lang = context.project.language,
                            underTestCode = originalContent,
                            generatedCode = function.Content,
                            coreFrameworks = context.project.coreFrameworks,
                            testFrameworks = context.project.testFrameworks,
                            testType = TestCodeBuilderType.METHOD_UNIT,
                            specs = listOf(
                                "Test class should be named `${namingStyle}`."
                            )
                        )

                        results[canonicalName] = results[canonicalName]?.plus(testIns) ?: listOf(testIns)
                    }
                }
            }
        }

        return results.values.flatten()
    }
}
