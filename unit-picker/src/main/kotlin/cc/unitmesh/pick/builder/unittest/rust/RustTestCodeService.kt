package cc.unitmesh.pick.builder.unittest.rust

import cc.unitmesh.core.SupportedLang
import cc.unitmesh.core.ast.NodeIdentifier
import cc.unitmesh.core.ast.NodeType
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.core.unittest.TypedTestIns
import cc.unitmesh.pick.builder.unittest.base.BasicTestIns
import cc.unitmesh.pick.builder.unittest.base.UnitTestService
import cc.unitmesh.pick.project.spec.checkNamingStyle
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeContainer
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction

class RustTestCodeService(val job: JobContext) : UnitTestService {
    override fun isApplicable(dataStruct: CodeDataStruct): Boolean = false

    override fun isApplicable(container: CodeContainer): Boolean = job.project.language == SupportedLang.RUST

    override fun build(container: CodeContainer): List<TypedTestIns> {
        // TODO: handle package error issues
        val functionMap = container.DataStructures.map {
            it.Functions.associateBy(::buildCanonicalName)
        }

        val testCode = container.DataStructures.map { dataStruct ->
            dataStruct.Functions.filter { function -> function.Annotations.any { it.Name == "test" } }
        }.flatten()

        if (testCode.isEmpty()) return emptyList()

        val namingStyle = container.DataStructures.first().checkNamingStyle()

        val result = testCode.map { codeFunction ->
            codeFunction.FunctionCalls.mapNotNull { codeCall ->
                val canonicalName = buildCanonicalName(codeCall.Package, codeCall.FunctionName)
                val underTestFunction = functionMap.firstNotNullOfOrNull { it[canonicalName] }
                if (underTestFunction == null) {
                    null
                } else {
                    BasicTestIns(
                        identifier = NodeIdentifier(
                            type = NodeType.METHOD,
                            name = "Method " + underTestFunction.Name,
                        ),
                        language = job.project.language,
                        underTestCode = underTestFunction.Content,
                        generatedCode = codeFunction.Content,
                        coreFrameworks = job.project.coreFrameworks,
                        testFrameworks = job.project.testFrameworks,
                        testType = TestCodeBuilderType.METHOD_UNIT,
                        relatedCode = listOf(),
                        specs = listOf(
                            "Test class should be named `${namingStyle}`."
                        )
                    )
                }
            }
        }.flatten()

        return result
    }

    private fun buildCanonicalName(function: CodeFunction): String {
        return buildCanonicalName(function.Package, function.Name)
    }

    private fun buildCanonicalName(pkgName: String, funcName: String): String {
        val pkg = pkgName.ifEmpty { "lib" }
        return "$pkg::$funcName"
    }

    override fun build(dataStruct: CodeDataStruct): List<TypedTestIns> = emptyList()
}