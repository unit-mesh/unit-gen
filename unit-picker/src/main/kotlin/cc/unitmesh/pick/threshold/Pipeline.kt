package cc.unitmesh.pick.threshold

import org.slf4j.Logger

class Pipeline<T> {
    private val logger: Logger = org.slf4j.LoggerFactory.getLogger(ThresholdChecker::class.java)

    private val filters = mutableListOf<Filter<T>>()

    fun addFilter(filter: Filter<T>): Pipeline<T> {
        filters.add(filter)
        return this
    }

    fun process(data: T): Boolean {
        return filters.all {
            val result = it.filter(data)
            if (!result.result) {
                if (result.isCritical) {
                    logger.warn("not met filter: ${it.javaClass.simpleName}, reason: ${result.reason}")
                }
            }

            result.result
        }
    }
}