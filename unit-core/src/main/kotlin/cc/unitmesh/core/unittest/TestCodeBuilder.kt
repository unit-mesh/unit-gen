package cc.unitmesh.core.unittest

import chapi.domain.core.CodeDataStruct

interface TestCodeBuilder {
    fun build(dataStruct: CodeDataStruct, underTestFile: CodeDataStruct, relevantClasses: List<CodeDataStruct>): List<TypedTestIns>
}


