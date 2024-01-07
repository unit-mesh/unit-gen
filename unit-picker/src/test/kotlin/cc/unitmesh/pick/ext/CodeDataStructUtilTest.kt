package cc.unitmesh.pick.ext

import cc.unitmesh.pick.project.spec.checkNamingStyle
import chapi.ast.javaast.JavaAnalyser
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeField
import chapi.domain.core.CodeFunction
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

        assertEquals(expectedUml, uml)
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

    @Test
    fun should_return_camel_case_when_all_function_names_are_in_camel_case() {
        // given
        val sourceCode = """
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

        val codeDataStruct = JavaAnalyser().analysis(sourceCode, "").DataStructures[0]
        // when
        val result = codeDataStruct.checkNamingStyle()

        // then
        assertEquals("CamelCase", result)
    }

    @Test
    fun should_return_snake_case_when_all_function_names_are_in_snake_case() {
        // given
        val sourceCode = """
            package com.example.springboot;
            
            import org.springframework.web.bind.annotation.GetMapping;
            import org.springframework.web.bind.annotation.RestController;
            
            @RestController
            class HelloController {
            	@GetMapping("/blog/get")
            	public String index_name() {
            		return "Greetings from Spring Boot!";
            	}
            }
            """.trimIndent()

        val codeDataStruct = JavaAnalyser().analysis(sourceCode, "").DataStructures[0]
        // when
        val result = codeDataStruct.checkNamingStyle()

        // then
        assertEquals("snake_case", result)
    }
}
