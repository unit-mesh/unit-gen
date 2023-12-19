package cc.unitmesh.pick.prompt

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val prettyJson = Json {
    prettyPrint = true
}

@Serializable
data class Instruction(
    val instruction: String,
    val input: String,
    val output: String,
) {
    override fun toString(): String {
        throw Exception("we don't support toString() for Instruction, please call render()")
    }

    fun render(
        pretty: Boolean = false,
        mergeInput: Boolean = false,
    ): String {
        val instruction = if (mergeInput) {
            this.instruction + "\n" + this.input
        } else {
            this.instruction
        }

        return if (pretty) {
            prettyJson.encodeToString(serializer(), this.copy(instruction = instruction))
        } else {
            Json.encodeToString(serializer(), this.copy(instruction = instruction))
        }
    }

    companion object {
        /**
         * Takes a list of instructions and applies a strategy to determine which instructions to return.
         *
         * @param instructions the list of instructions to be processed
         * @return a new list of instructions based on the applied strategy
         */
        fun takeStrategy(instructions: List<Instruction>, maxCompletionInOneFile: Int): List<Instruction> {
            // if size is less than maxCompletionInOneFile, return all
            if (instructions.size <= maxCompletionInOneFile) {
                return instructions
            }

            // if maxCompletionInOneFile == 1, return the first one
            if (maxCompletionInOneFile == 1) {
                return listOf(instructions.first())
            }

            // if more than maxCompletionInOneFile, return the first maxCompletionInOneFile, and random the rest
            val first = instructions.take(1)
            val rest = instructions.drop(maxCompletionInOneFile)
            val randomRest = rest.shuffled().take(maxCompletionInOneFile - 1)
            return first + randomRest
        }
    }
}
