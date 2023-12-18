package cc.unitmesh.pick.prompt

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Instruction(
    val instruction: String,
    val input: String,
    val output: String,
) {
    override fun toString(): String {
        return Json.encodeToString(serializer(), this)
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
