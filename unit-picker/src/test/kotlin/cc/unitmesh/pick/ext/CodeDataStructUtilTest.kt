package cc.unitmesh.pick.ext

import chapi.ast.javaast.JavaAnalyser
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeField
import chapi.domain.core.CodeFunction
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CodeDataStructUtilTest {
    @Test
    fun `toUml should return UML class representation for CodeDataStruct`() {
        // Given
        val codeDataStruct = CodeDataStruct(
            NodeName = "TestClass",
            Fields = listOf(
                CodeField("Int", "", "id"),
                CodeField("String", "", "name")
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

        Assertions.assertEquals(expectedUml, uml)
    }

    @Test
    fun shouldBuildToOriginCode() {
        val originCode = """
            package com.example.springboot;
            
            import org.springframework.web.bind.annotation.GetMapping;
            import org.springframework.web.bind.annotation.RestController;
            
            @RestController
            class HelloController {
            	@GetMapping("/blog/get")
            	public String index() {
            		return "Greetings from Spring Boot!";
            	}
            }
            """.trimIndent()
        val codeContainer = JavaAnalyser().analysis(
            originCode,
            "HelloController.java"
        )
        codeContainer.buildSourceCode(originCode.lines())

        assertEquals(originCode, codeContainer.DataStructures[0].toSourceCode())
    }
}
