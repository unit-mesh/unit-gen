package org.unimesh.eval.picker.version

import kotlinx.coroutines.CoroutineScope

abstract class AbstractWorker {
    abstract suspend fun start(filePath: String)
}