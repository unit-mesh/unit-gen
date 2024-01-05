package cc.unitmesh.pick.worker.lang

import cc.unitmesh.pick.ext.CodeDataStructUtil
import cc.unitmesh.pick.worker.WorkerContext
import cc.unitmesh.pick.worker.base.LangWorker
import cc.unitmesh.pick.worker.job.InstructionFileJob
import chapi.ast.rustast.RustAnalyser
import org.slf4j.Logger
import java.io.File
import java.nio.file.Path

class RustWorker(override val workerContext: WorkerContext) : LangWorker {
    override val jobs: MutableList<InstructionFileJob> = mutableListOf()
    override val fileTree: HashMap<String, InstructionFileJob> = hashMapOf()
    override val logger: Logger = org.slf4j.LoggerFactory.getLogger(RustWorker::class.java)

    override fun prepareJob(job: InstructionFileJob) {
        this.jobs.add(job)
        try {
            val container = RustAnalyser().analysis(job.code, job.fileSummary.location)
            val relativePath = Path.of(workerContext.project.codeDir).relativize(Path.of(job.fileSummary.location))
            container.PackageName = container.PackageName.fixPrefix(workerContext.project.codeDir)
            container.FullName = relativePath.toString()

            // update error packageName in here with a path
            job.codeLines = job.code.lines()
            container.DataStructures.map { ds ->
                ds.FilePath = relativePath.toString()
                ds.Imports = container.Imports
                ds.NodeName = ds.NodeName.fixPrefix(workerContext.project.codeDir)
                ds.Package = container.PackageName

                ds.Content = CodeDataStructUtil.contentByPosition(job.codeLines, ds.Position)
                ds.Functions.map {
                    it.Package = it.Package.fixPrefix(workerContext.project.codeDir)
                    it.Content = CodeDataStructUtil.contentByPosition(job.codeLines, it.Position)
                    it.FunctionCalls.map { call ->
                        call.Package = call.Package.fixPrefix(workerContext.project.codeDir)
                        call.NodeName = call.NodeName.fixPrefix(workerContext.project.codeDir)
                    }
                }
            }

            job.container = container
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // TODO: remove follow code after we Update Chapi >= 2.2.4
    companion object {
        val LIB_RS = "lib.rs"
        val MAIN_RS = "main.rs"

        /**
         * Calculates the package name for a given file name in the Kotlin language.
         *
         * @param fileName the name of the file for which the package name needs to be calculated
         * @return the calculated package name as a string
         *
         * The `calculatePackageName` method is used to determine the package name based on the given file name in the Kotlin language. The package name is an essential part of organizing code in Kotlin and is used to group related classes and files together.
         *
         * The package name is derived from the file name using the following rules:
         * - If the file name is "src/main.rs", the package name will be "main".
         * - If the file name is "enfer_core/src/lib.rs", the package name will be "enfer_core".
         * - If the file name is "enfer_core/src/document.rs", the package name will be "enfer_core::document".
         *
         * It is important to note that the package name is calculated based on the file path and may differ from the actual package declaration in the file.
         *
         * Example usage:
         * ```kotlin
         * val fileName = "enfer_core/src/document.rs"
         * val packageName = calculatePackageName(fileName)
         * println(packageName) // Output: enfer_core::document
         * ```
         */
        fun calculatePackageName(fileName: String): String {
            val modulePath = fileName.substringBeforeLast("src")
                .substringBeforeLast(File.separator)

            val paths = fileName.substringAfterLast("src").split(File.separator)

            // if pathSize == 1, it means the file is in the root directory
            if (paths.size == 1) {
                return if (fileName.endsWith(LIB_RS) || fileName.endsWith(MAIN_RS)) {
                    ""
                } else {
                    fileName.substringBeforeLast(".")
                }
            }

            // if modulePath is empty, use paths as package names
            val packageName = paths
                .filter { it.isNotEmpty() && it != MAIN_RS && it != LIB_RS }
                .joinToString("::") { it.substringBeforeLast(".") }

            if (modulePath.isEmpty()) {
                return packageName
            }

            // if modulePath is not empty, use modulePath as package name
            if (packageName.isEmpty()) {
                return modulePath
            }

            return "$modulePath::$packageName"
        }
    }
}

private fun String.fixPrefix(codeDir: String): String {
    var result = this;
    if (this.startsWith(codeDir)) {
        result = result.substring(codeDir.length + 1)
    }

    val pkgName = RustWorker.calculatePackageName(codeDir)
    // codeDir remove last '/'
    val codeDirParent = codeDir.substringBeforeLast("/")
    val libPrefix = pkgName.removePrefix(codeDirParent)

    if (this.contains(libPrefix)) {
        result = result.substringAfter(libPrefix)
    }

    return result.removePrefix("::")
}
