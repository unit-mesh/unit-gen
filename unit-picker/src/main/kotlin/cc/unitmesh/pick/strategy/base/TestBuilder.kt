package cc.unitmesh.pick.strategy.base

import cc.unitmesh.core.unittest.TypedTestIns

interface TestBuilder {
    fun build(): List<TypedTestIns>
}


