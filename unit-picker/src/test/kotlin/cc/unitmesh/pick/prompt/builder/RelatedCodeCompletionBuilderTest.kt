package cc.unitmesh.pick.prompt.builder;

import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeField
import chapi.domain.core.CodeFunction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CodeDataStructTest {
    @Test
    fun `toUml should return UML class representation for CodeDataStruct`() {
        // Given
        val codeDataStruct = CodeDataStruct(
            NodeName = "TestClass",
            Fields = listOf(
                CodeField("Int", "id"),
                CodeField("String", "name")
            ),
            Functions = listOf(
                CodeFunction("getName", "", ""),
                CodeFunction("setName", "", "")
            )
        )

        // When
        val uml = codeDataStruct.toUml()

        // Then
        val expectedUml = """
            |// class TestClass {
            |//    id: Int
            |//    name: String
            |// 
            |//    'getter/setter: getName
            |// 
            |//    + setName()
            |//  }
            |// 
        """.trimMargin()

        assertEquals(expectedUml, uml)
    }
}
