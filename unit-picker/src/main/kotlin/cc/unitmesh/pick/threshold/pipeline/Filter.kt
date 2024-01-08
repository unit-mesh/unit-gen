package cc.unitmesh.pick.threshold.pipeline

interface Filter<T> {
    fun filter(data: T): FilterResult
}