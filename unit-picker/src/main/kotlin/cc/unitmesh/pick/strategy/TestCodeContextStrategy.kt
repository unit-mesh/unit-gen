package cc.unitmesh.pick.strategy

import cc.unitmesh.pick.strategy.base.CodeStrategyBuilder
import cc.unitmesh.pick.strategy.unittest.ApiTestStrategyBuilder
import cc.unitmesh.pick.strategy.unittest.ClassTestStrategyBuilder
import cc.unitmesh.pick.strategy.unittest.MethodTestStrategyBuilder
import cc.unitmesh.pick.worker.job.JobContext
import kotlinx.serialization.SerializationException

enum class TestCodeContextStrategy {
    METHOD_UNIT,
    CLASS_UNIT,
    // TODO: spike for api unit test module
    API_UNIT;

    fun builder(context: JobContext): CodeStrategyBuilder {
        return mapOf(
            METHOD_UNIT to MethodTestStrategyBuilder(context),
            CLASS_UNIT to ClassTestStrategyBuilder(context),
            API_UNIT to ApiTestStrategyBuilder(context),
        )[this] ?: throw SerializationException("Unknown message type: $this")
    }
}

