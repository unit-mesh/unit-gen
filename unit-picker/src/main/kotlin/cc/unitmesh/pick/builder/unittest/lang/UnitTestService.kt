package cc.unitmesh.pick.builder.unittest.lang

import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.core.SupportedLang
import cc.unitmesh.core.unittest.TypedTestIns
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeDataStruct

/**
 * UnitTestService is an interface that provides methods for finding files containing under test code,
 * looking up relevant code data structures, and finding relevant classes for a given code function and data structure.
 *
 * The UnitTestService interface is used to facilitate the testing process by providing
 * functionality to locate and retrieve the necessary code structures.
 * It allows users to find files containing under test code, lookup relevant code data structures,
 * and find relevant classes for specific code functions and data structures.
 *
 * Implementations of the UnitTestService interface should provide the implementation
 * for these methods based on the specific requirements of the testing framework or tool being used.
 */
interface UnitTestService {
    /**
     * Checks if the given data structure is a test file.
     */
    fun isApplicable(dataStruct: CodeDataStruct): Boolean

    fun build(dataStructs: List<CodeDataStruct>): List<TypedTestIns>

    companion object {
        fun lookup(codeDataStruct: CodeDataStruct, job: JobContext): List<JavaTestCodeService> {
            val testCodeServices = SupportedLang.all().map {
                when (it) {
                    SupportedLang.JAVA -> JavaTestCodeService(job)
                }
            }

            return testCodeServices.filter { it.isApplicable(codeDataStruct) }
        }
    }
}

