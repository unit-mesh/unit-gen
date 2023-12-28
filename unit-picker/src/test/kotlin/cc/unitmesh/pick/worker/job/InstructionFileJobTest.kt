package cc.unitmesh.pick.worker.job

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test;

class InstructionFileJobTest {

    @Test
    fun shouldRemoveCommentsInJava() {
        val javaCode = """
        // This is a single-line comment
        /* This is a
         * multi-line comment 
         */
        public class MyClass {
            // This is another comment
            public void myMethod() {
                // This is an inline comment
                System.out.println("Hello, World!");
            }
        }
    """.trimIndent()

        val codeWithoutComments = removeMultipleComments("Java", javaCode)

        codeWithoutComments shouldBe """// This is a single-line comment

public class MyClass {
    // This is another comment
    public void myMethod() {
        // This is an inline comment
        System.out.println("Hello, World!");
    }
}"""
    }
}