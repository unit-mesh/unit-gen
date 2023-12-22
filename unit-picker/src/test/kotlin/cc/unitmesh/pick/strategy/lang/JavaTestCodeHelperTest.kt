package cc.unitmesh.pick.strategy.lang;

import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.*
import org.archguard.scanner.analyser.count.FileJob
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class JavaTestCodeHelperTest {
    @Test
    fun should_return_empty_list_when_fileTree_is_empty_for_findUnderTestFile() {
        // given
        val fileTree = hashMapOf<String, InstructionFileJob>()
        val context = JobContext.default(fileTree = fileTree)
        val codeHelper = JavaTestCodeHelper(context)
        val dataStruct = CodeDataStruct(NodeName = "TestClass")

        // when
        val result = codeHelper.findUnderTestFile(dataStruct)

        // then
        assertEquals(0, result.size)
    }

    @Test
    fun should_return_list_of_matching_dataStructures_when_testClass_present_in_fileTree_for_findUnderTestFile() {
        // given
        val fileTree = hashMapOf(
            "file.TestClass" to InstructionFileJob(
                fileSummary = FileJob(),
                container = CodeContainer(
                    DataStructures = listOf(
                        CodeDataStruct(NodeName = "TestClass")
                    )
                )
            ),
            "file.TestClassTest" to InstructionFileJob(
                fileSummary = FileJob(),
                container = CodeContainer(
                    DataStructures = listOf(
                        CodeDataStruct(NodeName = "TestClassTest")
                    )
                )
            )
        )
        val context = JobContext.default(fileTree = fileTree)
        val codeHelper = JavaTestCodeHelper(context)
        val dataStruct = CodeDataStruct(NodeName = "TestClassTest")

        // when
        val result = codeHelper.findUnderTestFile(dataStruct)

        // then
        assertEquals(1, result.size)
        assertEquals("TestClass", result[0].NodeName)
    }

    @Test
    fun should_return_empty_list_when_imports_are_empty_for_lookupRelevantClass() {
        // given
        val fileTree = hashMapOf<String, InstructionFileJob>()
        val context = JobContext.default(fileTree = fileTree)
        val codeHelper = JavaTestCodeHelper(context)
        val dataStruct = CodeDataStruct(Imports = emptyList())

        // when
        val result = codeHelper.lookupRelevantClass(dataStruct)

        // then
        assertEquals(0, result.size)
    }

    @Test
    fun should_return_empty_list_when_imports_do_not_have_matching_fileTree_entries_for_lookupRelevantClass() {
        // given
        val fileTree = hashMapOf(
            "OtherFile.kt" to InstructionFileJob(
                fileSummary = FileJob(),
                container = CodeContainer(
                    DataStructures = listOf(
                        CodeDataStruct(NodeName = "OtherClass")
                    )
                )
            )
        )
        val context = JobContext.default(fileTree = fileTree)
        val codeHelper = JavaTestCodeHelper(context)
        val dataStruct = CodeDataStruct(
            Imports = listOf(
                CodeImport(Source = "Import1"),
                CodeImport(Source = "Import2")
            )
        )

        // when
        val result = codeHelper.lookupRelevantClass(dataStruct)

        // then
        assertEquals(0, result.size)
    }

    @Test
    fun should_return_list_of_matching_dataStructures_when_imports_have_matching_fileTree_entries_for_lookupRelevantClass() {
        // given
        val fileTree = hashMapOf(
            "File1.kt" to InstructionFileJob(
                fileSummary = FileJob(),
                container = CodeContainer(
                    DataStructures = listOf(
                        CodeDataStruct(NodeName = "Class1")
                    )
                )
            ),
            "File2.kt" to InstructionFileJob(
                fileSummary = FileJob(),
                container = CodeContainer(
                    DataStructures = listOf(
                        CodeDataStruct(NodeName = "Class2")
                    )
                )
            ),
            "File3.kt" to InstructionFileJob(
                fileSummary = FileJob(),
                container = CodeContainer(
                    DataStructures = listOf(
                        CodeDataStruct(NodeName = "Class3")
                    )
                )
            )
        )
        val context = JobContext.default(fileTree = fileTree)
        val codeHelper = JavaTestCodeHelper(context)
        val dataStruct = CodeDataStruct(
            Imports = listOf(
                CodeImport(Source = "File1.kt"),
                CodeImport(Source = "File2.kt")
            )
        )

        // when
        val result = codeHelper.lookupRelevantClass(dataStruct)

        // then
        assertEquals(2, result.size)
        assertEquals("Class1", result[0].NodeName)
        assertEquals("Class2", result[1].NodeName)
    }

    @Test
    fun should_return_empty_list_when_imports_and_parameters_are_empty_for_lookupRelevantClass() {
        // given
        val fileTree = hashMapOf<String, InstructionFileJob>()
        val context = JobContext.default(fileTree = fileTree)
        val codeHelper = JavaTestCodeHelper(context)
        val codeFunction = CodeFunction(ReturnType = "ReturnType")
        val dataStruct = CodeDataStruct(Imports = emptyList())

        // when
        val result = codeHelper.lookupRelevantClass(codeFunction, dataStruct)

        // then
        assertEquals(0, result.size)
    }

    @Test
    fun should_return_empty_list_when_imports_do_not_have_matching_fileTree_entries_for_lookupRelevantClass_with_parameters() {
        // given
        val fileTree = hashMapOf(
            "OtherFile.kt" to InstructionFileJob(
                fileSummary = FileJob(),
                container = CodeContainer(
                    DataStructures = listOf(
                        CodeDataStruct(NodeName = "OtherClass")
                    )
                )
            )
        )
        val context = JobContext.default(fileTree = fileTree)
        val codeHelper = JavaTestCodeHelper(context)
        val codeFunction = CodeFunction(
            ReturnType = "ReturnType",
            Parameters = listOf(
                CodeProperty(TypeType = "Parameter1", TypeValue = "p1"),
                CodeProperty(TypeType = "Parameter2", TypeValue = "p2")
            )
        )
        val dataStruct = CodeDataStruct(
            Imports = listOf(
                CodeImport(Source = "Import1"),
                CodeImport(Source = "Import2")
            )
        )

        // when
        val result = codeHelper.lookupRelevantClass(codeFunction, dataStruct)

        // then
        assertEquals(0, result.size)
    }

    @Test
    fun should_return_list_of_matching_dataStructures_when_imports_have_matching_fileTree_entries_for_lookupRelevantClass_with_parameters() {
        // given
        val fileTree = hashMapOf(
            "file1.Class1" to InstructionFileJob(
                fileSummary = FileJob(),
                container = CodeContainer(
                    DataStructures = listOf(
                        CodeDataStruct(NodeName = "Class1", Package = "file1")
                    )
                )
            ),
            "file2.Class2" to InstructionFileJob(
                fileSummary = FileJob(),
                container = CodeContainer(
                    DataStructures = listOf(
                        CodeDataStruct(NodeName = "Class2", Package = "file2")
                    )
                )
            ),
            "file2.Class3" to InstructionFileJob(
                fileSummary = FileJob(),
                container = CodeContainer(
                    DataStructures = listOf(
                        CodeDataStruct(NodeName = "Class3", Package = "file3")
                    )
                )
            )
        )
        val context = JobContext.default(fileTree = fileTree)
        val codeHelper = JavaTestCodeHelper(context)
        val codeFunction = CodeFunction(
            ReturnType = "Class3",
            Parameters = listOf(
                CodeProperty(TypeType = "Class1", TypeValue = "p1"),
                CodeProperty(TypeType = "Class2", TypeValue = "p2")
            )
        )
        val dataStruct = CodeDataStruct(
            Imports = listOf(
                CodeImport(Source = "file1.Class1"),
                CodeImport(Source = "file2.Class2")
            )
        )

        // when
        val result = codeHelper.lookupRelevantClass(codeFunction, dataStruct)

        // then
        assertEquals(2, result.size)
        assertEquals("Class1", result[0].NodeName)
        assertEquals("Class2", result[1].NodeName)
    }

    @Test
    fun should_return_matching_dataStructures_from_fileTree_for_filterDs() {
        // given
        val fileTree = hashMapOf(
            "file1.Class1" to InstructionFileJob(
                fileSummary = FileJob(),
                container = CodeContainer(
                    DataStructures = listOf(
                        CodeDataStruct(NodeName = "Class1", Package = "file1")
                    )
                )
            ),
            "file2.Class2" to InstructionFileJob(
                fileSummary = FileJob(),
                container = CodeContainer(
                    DataStructures = listOf(
                        CodeDataStruct(NodeName = "Class2", Package = "file2")
                    )
                )
            ),
            "file2.Class3" to InstructionFileJob(
                fileSummary = FileJob(),
                container = CodeContainer(
                    DataStructures = listOf(
                        CodeDataStruct(NodeName = "Class3", Package = "file3")
                    )
                )
            )
        )
        val imports = listOf(
            CodeImport(Source = "file1.Class1"),
            CodeImport(Source = "file2.Class2")
        )
        val returnType = "Class1"
        val codeHelper = JavaTestCodeHelper(JobContext.default(fileTree = fileTree))

        // when
        val result = codeHelper.filterDs(imports, returnType, fileTree)

        // then
        assertEquals(1, result.size)
        assertEquals("Class1", result[0].NodeName)
    }
}
