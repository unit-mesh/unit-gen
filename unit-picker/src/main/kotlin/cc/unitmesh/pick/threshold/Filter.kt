package cc.unitmesh.pick.threshold

interface Filter<T> {
    fun filter(data: T): Boolean
}