package cc.unitmesh.core.intelli

import cc.unitmesh.language.LanguageService;

class LangService {
    private val langService = LanguageService();

    fun lineComment(lang: String): String {
        return langService.guessLineComment(lang) ?: "//"
    }

    companion object {
        private var instance: LangService? = null
        fun getInstance(): LangService {
            if (instance == null) {
                instance = LangService()
            }
            return instance!!
        }
    }
}