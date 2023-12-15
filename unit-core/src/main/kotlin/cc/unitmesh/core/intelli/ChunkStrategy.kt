package cc.unitmesh.core.intelli

/**
 * Recent file strategy:
 *
 * - Strategy 1: Analysis by imports. This strategy analyzes the code by its imports and finds similar chunks.
 * - Strategy 2: Based on similarity in file name. This strategy identifies similar chunks based on similarities
 *   found in the file names.
 *
 * Chunk Strategy:
 * - Strategy 1: Analysis of the Abstract Syntax Tree (AST) of the code. This strategy splits the code into chunks
 *   based on the AST and then finds similar chunks.
 * - Strategy 2: Splitting the code by lines. This strategy splits the code into chunks based on line-breaks and
 *   then finds similar chunks.
 *
 */
enum class SimilarPathStrategy {
    FILE_IMPORTS,
    SIMILAR_FILE_NAME,

    // just to make sure the concept is clear
    RECENT_FILES,
}

enum class ChunkCodeStrategy {
    AST,
    LINES,
}
