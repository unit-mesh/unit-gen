package org.unimesh.eval.picker.version

import kotlinx.coroutines.coroutineScope
import org.archguard.scanner.analyser.count.FileJob

class JavaFileWorker {
    val packageTree: Map<String, FileJob> = mapOf()

    suspend fun start(filePath: String) = coroutineScope {
        // 1. read directory to a collection of files for FileJob

        // 2. check package information from line 1?

        // 3. build full project trees
    }
}