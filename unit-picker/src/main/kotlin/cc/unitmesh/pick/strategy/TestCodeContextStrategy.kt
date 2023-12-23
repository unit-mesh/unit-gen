package cc.unitmesh.pick.strategy

import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.pick.strategy.base.TestStrategyBuilder
import cc.unitmesh.pick.strategy.unittest.ApiTestStrategyBuilder
import cc.unitmesh.pick.strategy.unittest.ClassTestStrategyBuilder
import cc.unitmesh.pick.strategy.unittest.MethodTestStrategyBuilder
import cc.unitmesh.pick.worker.job.JobContext
import kotlinx.serialization.SerializationException

fun testBuilder(context: JobContext, type: TestCodeBuilderType): TestStrategyBuilder {
    return mapOf(
        TestCodeBuilderType.METHOD_UNIT to MethodTestStrategyBuilder(context),
        TestCodeBuilderType.CLASS_UNIT to ClassTestStrategyBuilder(context),
        TestCodeBuilderType.API_UNIT to ApiTestStrategyBuilder(context),
    )[type] ?: throw SerializationException("Unknown message type: $type")
}

