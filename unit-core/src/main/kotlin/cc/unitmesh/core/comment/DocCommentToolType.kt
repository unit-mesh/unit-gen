package cc.unitmesh.core.comment

enum class DocCommentToolType(val value: String) {
    CPP("doxygen"),
    JAVA("javadoc"),
    JAVASCRIPT("JSDoc"),
    PHP("PHPDoc"),
    GO("Go Doc"),
    RUBY("YARD documentation"),
    KOTLIN("KDoc")
}