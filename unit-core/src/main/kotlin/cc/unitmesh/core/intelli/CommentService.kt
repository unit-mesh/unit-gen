package cc.unitmesh.core.intelli

import cc.unitmesh.language.LanguageService;

class CommentService {
    private val langService = LanguageService();

    fun lineComment(lang: String): String {
        return langService.guessLineComment(lang) ?: "//"
    }

    companion object {
        private var instance: CommentService? = null
        fun getInstance(): CommentService {
            if (instance == null) {
                instance = CommentService()
            }
            return instance!!
        }
    }
}