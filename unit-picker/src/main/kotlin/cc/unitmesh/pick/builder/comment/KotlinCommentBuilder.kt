package cc.unitmesh.pick.builder.comment

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.comment.*
import cc.unitmesh.pick.ext.toUml
import chapi.domain.core.CodeContainer
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction
import chapi.domain.core.CodePosition
import kotlinx.serialization.Serializable

class KotlinCommentBuilder : CommentBuilder {
    override val commentStart: String = "/**"
    override val commentEnd: String = "*/"
    override val docInstruction: DocInstruction = DocInstruction.KOTLIN

    override fun build(code: String, container: CodeContainer): List<TypedCommentIns> {
        val posComments = try {
            extractKdocComments(code)
        } catch (e: Exception) {
            emptyList()
        }

        val startLineCommentMap: Map<Int, CodeComment> = posComments.associateBy {
            it.position.StopLine
        }


        val comments = mutableListOf<TypedCommentIns>()

        container.DataStructures.forEach { dataStruct ->
            val classComment = startLineCommentMap[dataStruct.Position.StartLine - 1]
            classComment?.let { comments.add(ClassCommentIns(docInstruction, dataStruct, it, language = "kotlin")) }

            dataStruct.Functions
                .filter { it.Name != "constructor" && it.Name != "PrimaryConstructor" }
                .forEach { function ->
                    val functionComment = startLineCommentMap[function.Position.StartLine - 1]
                    functionComment?.let {
                        comments.add(
                            MethodCommentIns(
                                docInstruction,
                                function,
                                it,
                                dataStruct,
                                language = "kotlin"
                            )
                        )
                    }
                }
        }

        return comments
    }

    /**
     * Extracts the Kotlin documentation comments (KDoc) from the given code.
     *
     * @param code the Kotlin code from which to extract the KDoc comments
     * @return a list of pairs, where each pair contains the line number and the extracted KDoc comment
     */
    fun extractKdocComments(code: String): List<CodeComment> {
        val pattern = Regex("""/\*\*[^*]*\*+([^/*][^*]*\*+)*/""")

        val matches = pattern.findAll(code)

        val comments = mutableListOf<CodeComment>()

        for (match in matches) {
            val commentContent = match.value.trimIndent()
            val startLine = code.substring(0, match.range.first).count { it == '\n' } + 1
            val stopLine = code.substring(0, match.range.last).count { it == '\n' } + 1
            val startLinePosition = match.range.first - code.lastIndexOf('\n', match.range.first) - 1
            val stopLinePosition = match.range.last - code.lastIndexOf('\n', match.range.last) - 1

            val position = CodePosition(startLine, startLinePosition, stopLine, stopLinePosition)
            val content = reIndentComment(commentContent)


            val comment = CodeComment(content, position)
            comments.add(comment)
        }

        return comments
    }

    /// Re-indent the comment to remove the leading spaces.
    private fun reIndentComment(content: String): String {
        val lines = content.split("\n")
        val indent = lines[1].takeWhile { it == ' ' }
        val linesWithoutIndent = lines
            .map { it.removePrefix(indent) }

        // except the first line, every line should have one leading space
        val linesWithLeadingSpace = linesWithoutIndent
            .mapIndexed { index, line ->
                if (index == 0) {
                    line
                } else {
                    " $line"
                }
            }

        return linesWithLeadingSpace.joinToString("\n")
    }
}

@Serializable
data class ClassCommentIns(
    val docInstruction: DocInstruction,
    val dataStructure: CodeDataStruct,
    val comment: CodeComment,
    val language: String,
) : TypedCommentIns() {
    override val builderLevel: CommentBuilderType = CommentBuilderType.CLASS_LEVEL

    override fun unique(): Instruction {
        val instruction = "Write documentation for given class " + dataStructure.NodeName + " .\n"
        val input = "Code:\n```$language\n" + dataStructure.Content + "\n```"
        val output = comment.content

        return Instruction(instruction, input, output)
    }
}

@Serializable
data class MethodCommentIns(
    val docInstruction: DocInstruction,
    val function: CodeFunction,
    val comment: CodeComment,
    val currentDataStruct: CodeDataStruct,
    val language: String,
) : TypedCommentIns() {
    override val builderLevel: CommentBuilderType = CommentBuilderType.METHOD_LEVEL

    override fun unique(): Instruction {
        val instruction = "Write documentation for given method " + function.Name + " .\n"
        val input = "### Current class:\n" + currentDataStruct.toUml() + "\n###\n" + "Code:\n```$language\n" + currentDataStruct.Content + "\n```"
        val output = comment.content

        return Instruction(instruction, input, output)
    }
}