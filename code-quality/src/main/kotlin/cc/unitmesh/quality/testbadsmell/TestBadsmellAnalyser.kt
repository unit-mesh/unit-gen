package cc.unitmesh.quality.testbadsmell

import cc.unitmesh.quality.QualityAnalyser
import chapi.domain.core.CodeAnnotation
import chapi.domain.core.CodeCall
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction
import org.archguard.rule.core.Issue

/**
 * The `TestBadsmellAnalyser` class is responsible for analyzing code data structures and identifying various bad smells in test code.
 * It implements the `QualityAnalyser` interface.
 *
 * @property thresholds A map of thresholds for different bad smells. The keys represent the types of bad smells, and the values represent the corresponding thresholds.
 * @constructor Creates a `TestBadsmellAnalyser` instance with optional thresholds.
 */
class TestBadsmellAnalyser(val thresholds: Map<String, Int> = mapOf()) : QualityAnalyser {
    private val ASSERT_PARAMETER_SIZE = 2
    private val DUPLICATED_LIMIT_SIZE = 5

    /**
     * This method performs analysis on a list of test code data structures and returns a list of issues found.
     *
     * @param nodes The list of code data structures to be analyzed.
     * @return A list of issues found during the analysis.
     */
    override fun analysis(nodes: List<CodeDataStruct>): List<Issue> {
        val tbsResult = TbsResult(listOf())
        val callMethodMap = buildCallMethodMap(nodes)

        for (node in nodes) {
            for (method in node.Functions) {
                if (!method.isJUnitTest()) {
                    continue
                }

                val currentMethodCalls = addExtractAssertMethodCall(method, node, callMethodMap)
                method.FunctionCalls = currentMethodCalls

                for (annotation in method.Annotations) {
                    checkIgnoreTest(node.FilePath, annotation, tbsResult, method)
                    checkEmptyTest(node.FilePath, annotation, tbsResult, method)
                }

                val methodCallMap = hashMapOf<String, Array<CodeCall>>()
                var hasAssert = false

                for ((index, funcCall) in currentMethodCalls.withIndex()) {
                    if (funcCall.FunctionName == "") {
                        val lastFuncCall = index == currentMethodCalls.size - 1
                        if (lastFuncCall && !hasAssert) {
                            appendUnknownTest(node.FilePath, method, tbsResult)
                        }
                        continue
                    }

                    updateMethodCallMap(funcCall, methodCallMap)

                    checkRedundantPrintTest(node.FilePath, funcCall, tbsResult)
                    checkSleepyTest(node.FilePath, funcCall, tbsResult)
                    checkRedundantAssertionTest(node.FilePath, funcCall, tbsResult)

                    if (funcCall.hasAssertion()) hasAssert = true

                    val lastFuncCall = index == currentMethodCalls.size - 1
                    if (lastFuncCall && !hasAssert) {
                        appendUnknownTest(node.FilePath, method, tbsResult)
                    }
                }

                checkDuplicateAssertTest(node, method, methodCallMap, tbsResult)
            }
        }

        return tbsResult.results.map { it.toIssue() }.toMutableList()
    }

    private fun addExtractAssertMethodCall(
        method: CodeFunction,
        node: CodeDataStruct,
        callMethodMap: MutableMap<String, CodeFunction>,
    ): List<CodeCall> {
        var methodCalls = method.FunctionCalls
        for (methodCall in methodCalls) {
            if (methodCall.NodeName == node.NodeName) {
                val mapFunc = callMethodMap[methodCall.buildFullMethodName()]
                if (mapFunc != null && mapFunc.Name != "") {
                    methodCalls += mapFunc.FunctionCalls
                }
            }
        }

        return methodCalls
    }

    private fun updateMethodCallMap(
        funcCall: CodeCall,
        methodCallMap: HashMap<String, Array<CodeCall>>,
    ) {
        var calls: Array<CodeCall> = arrayOf()
        val buildFullMethodName = funcCall.buildFullMethodName()
        if (methodCallMap[buildFullMethodName] != null) {
            calls = methodCallMap[buildFullMethodName]!!
        }

        calls += funcCall
        methodCallMap[buildFullMethodName] = calls
    }

    private fun checkDuplicateAssertTest(
        node: CodeDataStruct,
        method: CodeFunction,
        methodCallMap: MutableMap<String, Array<CodeCall>>,
        tbsResult: TbsResult,
    ) {
        var isDuplicateTest = false
        for (entry in methodCallMap) {
            val methodCalls = entry.value
            if (methodCalls.size >= DUPLICATED_LIMIT_SIZE) {
                if (methodCalls.last().hasAssertion()) {
                    isDuplicateTest = true
                }
            }
        }

        if (isDuplicateTest) {
            tbsResult.results += TestBadSmell(
                fileName = node.FilePath,
                type = "DuplicateAssertTest",
                description = "",
                line = method.Position.StartLine
            )
        }
    }

    private fun appendUnknownTest(filePath: String, method: CodeFunction, tbsResult: TbsResult) {
        tbsResult.results += TestBadSmell(
            fileName = filePath, type = "UnknownTest", description = "", line = method.Position.StartLine
        )
    }

    private fun checkRedundantAssertionTest(filePath: String, funcCall: CodeCall, tbsResult: TbsResult) {
        if (funcCall.Parameters.size != ASSERT_PARAMETER_SIZE) return

        if (funcCall.Parameters[0].TypeValue != funcCall.Parameters[1].TypeValue) return

        tbsResult.results += TestBadSmell(
            fileName = filePath, type = "RedundantAssertionTest", description = "", line = funcCall.Position.StartLine
        )
    }

    private fun checkSleepyTest(filePath: String, funcCall: CodeCall, tbsResult: TbsResult) {
        if (!funcCall.isThreadSleep()) return
        tbsResult.results += TestBadSmell(
            fileName = filePath, type = "SleepyTest", description = "", line = funcCall.Position.StartLine
        )
    }

    private fun checkRedundantPrintTest(filePath: String, funcCall: CodeCall, tbsResult: TbsResult) {
        if (!funcCall.isSystemOutput()) return
        tbsResult.results += TestBadSmell(
            fileName = filePath, type = "RedundantPrintTest", description = "", line = funcCall.Position.StartLine
        )
    }

    private fun checkIgnoreTest(
        path: String,
        annotation: CodeAnnotation,
        tbsResult: TbsResult,
        method: CodeFunction,
    ) {
        if (!annotation.isIgnore()) return
        tbsResult.results += TestBadSmell(
            fileName = path, type = "IgnoreTest", description = "", line = method.Position.StartLine
        )
    }

    private fun checkEmptyTest(
        filePath: String,
        annotation: CodeAnnotation,
        tbsResult: TbsResult,
        method: CodeFunction,
    ) {
        val isJavaTest = filePath.endsWith(".java") && annotation.isTest()
        val isGoTest = filePath.endsWith("_test.go")
        if (!isJavaTest && !isGoTest) return
        if (method.FunctionCalls.size > 1) return

        tbsResult.results += TestBadSmell(
            fileName = filePath,
            type = "EmptyTest",
            description = "",
            line = method.Position.StartLine
        )
    }

    private fun buildCallMethodMap(nodes: List<CodeDataStruct>): MutableMap<String, CodeFunction> {
        val callMethodMap: MutableMap<String, CodeFunction> = mutableMapOf()
        for (node in nodes) {
            for (method in node.Functions) {
                callMethodMap[method.buildFullMethodName(node)] = method
            }
        }

        return callMethodMap
    }
}