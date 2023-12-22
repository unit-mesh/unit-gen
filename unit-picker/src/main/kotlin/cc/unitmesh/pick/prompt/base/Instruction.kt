package cc.unitmesh.pick.prompt.base

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val prettyJson = Json {
    prettyPrint = true
}

@Serializable
data class SimpleInstruction(
    val instruction: String,
    val output: String,
)

@Serializable
data class Instruction(
    var instruction: String,
    var input: String,
    val output: String,
) {
    override fun toString(): String {
        throw Exception("we don't support toString() for Instruction, please call render()")
    }

    fun render(
        pretty: Boolean = false,
        mergeInput: Boolean = false,
    ): String {
        if (mergeInput) {
            val simpleInstruction = SimpleInstruction(
                instruction = this.instruction.trim() + "\n" + this.input.trim(),
                output = this.output.trim(),
            )

            return simpleInstruction.let {
                if (pretty) {
                    prettyJson.encodeToString(SimpleInstruction.serializer(), it)
                } else {
                    Json.encodeToString(SimpleInstruction.serializer(), it)
                }
            }
        }

        return if (pretty) {
            prettyJson.encodeToString(serializer(), this)
        } else {
            Json.encodeToString(serializer(), this)
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

            // if maxCompletionInOneFile == 1, return the last one, the first one almost `constructor` function
            if (maxCompletionInOneFile == 1) {
                return listOf(instructions.last())
            }

            // if more than maxCompletionInOneFile, return the first maxCompletionInOneFile, and random the rest
            val first: List<Instruction> = listOf(instructions.last())
            val rest = instructions.drop(maxCompletionInOneFile)
            val randomRest = rest.shuffled().take(maxCompletionInOneFile - 1)
            return first + randomRest
        }
    }
}
