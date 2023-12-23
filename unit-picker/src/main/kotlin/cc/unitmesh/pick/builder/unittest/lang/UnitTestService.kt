package cc.unitmesh.pick.builder.unittest.lang

import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.core.SupportedLang
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction

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

    /**
     * Finds the files that contain the under test code related to the given data structure.
     *
     * @param dataStruct The code data structure to find the under test files for.
     * @return A list of code data structures representing the files that contain the under test code
     *         related to the given data structure. If no under test files are found, an empty list is returned.
     */
    fun findUnderTestFile(dataStruct: CodeDataStruct): List<CodeDataStruct>

    /**
     * Looks up and returns a list of relevant code data structures based on the given code data structure.
     *
     * @param dataStruct The code data structure for which relevant classes need to be looked up.
     *
     * @return A list of code data structures that are relevant to the given code data structure.
     *         These relevant classes are determined by searching for imports in the context's file tree,
     *         and then retrieving their corresponding data structures.
     *         If an import cannot be found in the file tree or if its container does not contain any data structures,
     *         it will be ignored and not included in the resulting list.
     */
    fun lookupRelevantClass(dataStruct: CodeDataStruct): List<CodeDataStruct>

    /**
     * This method is used to look up the relevant classes for a given code function and code data structure.
     *
     * @param codeFunction The code function for which relevant classes need to be looked up.
     * @param dataStruct The code data structure containing information about imports and types.
     * @return A list of code data structures representing the relevant classes found for the given code function and data structure.
     */
    fun lookupRelevantClass(codeFunction: CodeFunction, dataStruct: CodeDataStruct): List<CodeDataStruct>

    fun build(): TypedIns

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

