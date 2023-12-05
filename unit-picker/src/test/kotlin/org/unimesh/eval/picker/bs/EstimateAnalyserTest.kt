package org.unimesh.eval.picker.bs;

import cc.unitmesh.quality.EstimateAnalyser
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class EstimateAnalyserTest {

    @Test
    fun should_return_null_when_analysis_by_content_with_empty_content() {
        // given
        val content = """type BSDataStruct struct {
	core_domain.CodeDataStruct

	Functions    []BSFunction
	DataStructBS ClassBadSmellInfo
}"""
        val file = File("sample.txt").canonicalPath

        // when
        val summary = EstimateAnalyser.getInstance().analysisByContent(content, file)!!

        // then
        println(summary)
        assertEquals("Go", summary.name)
    }
}
