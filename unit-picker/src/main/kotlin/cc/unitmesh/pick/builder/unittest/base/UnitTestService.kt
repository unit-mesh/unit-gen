package cc.unitmesh.pick.builder.unittest.base

import cc.unitmesh.core.SupportedLang
import cc.unitmesh.core.unittest.TypedTestIns
import cc.unitmesh.pick.builder.unittest.java.JavaTestCodeService
import cc.unitmesh.pick.builder.unittest.kotlin.KotlinTestCodeService
import cc.unitmesh.pick.builder.unittest.rust.RustTestCodeService
import cc.unitmesh.pick.builder.unittest.typescript.TypeScriptTestCodeService
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeContainer
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
     * For [SupportedLang.RUST] can analysis by container
     */
    fun isApplicable(container: CodeContainer): Boolean = false

    /**
     * For [SupportedLang.RUST], the file contains test code.
     */
    fun build(container: CodeContainer): List<TypedTestIns> = listOf()

    /**
     * Checks if the given data structure is a test file.
     */
    fun isApplicable(dataStruct: CodeDataStruct): Boolean

    /**
     * For [SupportedLang.JAVA], [SupportedLang.KOTLIN], can analysis by dataStruct
     */
    fun build(dataStruct: CodeDataStruct): List<TypedTestIns>

    companion object {
        fun lookup(codeDataStruct: CodeDataStruct, job: JobContext): List<UnitTestService> {
            val testCodeServices = unitTestServices(job)
            return testCodeServices.filter { it.isApplicable(codeDataStruct) }
        }

        fun lookup(codeContainer: CodeContainer, job: JobContext): List<UnitTestService> {
            val testCodeServices = unitTestServices(job)
            return testCodeServices.filter { it.isApplicable(codeContainer) }
        }

        private fun unitTestServices(job: JobContext): List<UnitTestService> {
            val testCodeServices = SupportedLang.all().map {
                when (it) {
                    SupportedLang.JAVA -> JavaTestCodeService(job)
                    SupportedLang.TYPESCRIPT -> TypeScriptTestCodeService(job)
                    SupportedLang.KOTLIN -> KotlinTestCodeService(job)
                    SupportedLang.RUST -> RustTestCodeService(job)
                }
            }
            return testCodeServices
        }
    }
}
