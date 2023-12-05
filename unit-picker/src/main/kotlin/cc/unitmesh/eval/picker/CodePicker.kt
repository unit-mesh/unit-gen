package cc.unitmesh.eval.picker

class CodePicker(val config: PickerConfig) {

    fun run() {
        // 1. check config.url is a valid url or path
        checkoutCode(config.url)

        // 2. select files to tree

        // 3. generate tree to jsonl
    }

    private fun checkoutCode(url: String) {
        if (gitUrlRegex.matches(url)) {
            // git clone
        } else {
            // copy
        }
    }

    companion object {
        val gitUrlRegex =
            """(git@|http://|https://)((?<host>[\w\.@]+)(/|:))(?<owner>[\w,\-,\_]+)/(?<repo>[\w,\-,\_]+)(.git){0,1}((/){0,1})""".toRegex()

        /**
         * Convert git url to path
         *
         * for example:
         *
         * - `https://github.com/unit-mesh/unit-eval` tobe `github.com/unit-mesh/unit-eval`
         * - `git://github.com/unit-mesh/unit-eval` tobe `github.com/unit-mesh/unit-eval`
         * - `git://github.com/unit-mesh/unit-eval.git` tobe `github.com/unit-mesh/unit-eval`
         * - `http://github.com/unit-mesh/unit-eval` tobe `github.com/unit-mesh/unit-eval`
         *
         */
        fun gitUrlToPath(url: String): String {
            val trimmedUrl = url.trim()
            val matchResult = gitUrlRegex.find(trimmedUrl) ?: throw IllegalArgumentException("invalid git url: $url")

            val host = matchResult.groups["host"]!!.value
            val owner = matchResult.groups["owner"]!!.value
            val repo = matchResult.groups["repo"]!!.value

            return "$host/$owner/$repo"
        }
    }
}
