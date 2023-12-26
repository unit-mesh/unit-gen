---
layout: default
title: Quality threshold
parent: Quality
nav_order: 1
permalink: /quality/threshold
---

threshold work processes:

- check input: InstructionFileJob
- check output: Instruction

```kotlin
class ThresholdChecker(private val context: WorkerContext) {
    private var registry: EncodingRegistry = Encodings.newDefaultEncodingRegistry()
    private var enc: Encoding = registry.getEncoding(EncodingType.CL100K_BASE)

    private val language: LanguageService = LanguageService()

    private val supportedExtensions: Set<String> = setOf(
        language.getExtension(SupportedLang.JAVA.name.lowercase()),
    )

    private val logger: Logger = org.slf4j.LoggerFactory.getLogger(ThresholdChecker::class.java)

    /**
     * Checks if the given job meets the threshold criteria for processing.
     *
     * @param job The instruction file job to be checked.
     * @return Returns true if the job meets the threshold criteria, false otherwise.
     */
    fun isMetThreshold(job: InstructionFileJob): Boolean {
        val summary = job.fileSummary
        if (!supportedExtensions.contains(summary.extension)) {
            return false
        }

        if (summary.complexity > context.qualityThreshold.complexity) {
            logger.info("skip file ${summary.location} for complexity ${summary.complexity}")
            return false
        }

        // like js minified file
        if (summary.binary || summary.generated || summary.minified) {
            return false
        }

        // if the file size is too large, we just try 64k
        if (summary.bytes > context.qualityThreshold.fileSize) {
            logger.info("skip file ${summary.location} for size ${summary.bytes}")
            return false
        }

        // limit by token length
        val encoded = enc.encode(job.code)
        val length = encoded.size
        if (length > context.qualityThreshold.maxTokenLength) {
            logger.info("skip file ${summary.location} for over ${context.qualityThreshold.maxTokenLength} tokens")
            println("| filename: ${summary.filename} |  tokens: $length | complexity: ${summary.complexity} | code: ${summary.lines} | size: ${summary.bytes} | location: ${summary.location} |")
            return false
        }

        return true
    }

    /**
     * Determines whether the given instruction meets the threshold criteria.
     *
     * @param ins the instruction to be evaluated
     * @return true if the instruction meets the threshold criteria, false otherwise
     */
    fun isMetThreshold(ins: Instruction): Boolean {
        val totalToken = enc.encode(ins.instruction + ins.input + ins.output).size
        if (totalToken > context.qualityThreshold.maxTokenLength) {
            logger.info("skip instruction ${ins.instruction} for over ${context.qualityThreshold.maxTokenLength} tokens")
            return false
        }

        return true
    }
}
```

## Default threshold

```kotlin
@Serializable
data class InsQualityThreshold(
    val complexity: Int = MAX_COMPLEXITY,
    val fileSize: Int = MAX_FILE_SIZE,
    /**
     * https://docs.sweep.dev/blogs/chunking-2m-files
     * This is because the average token to a character ratio for code is ~1:5(300 tokens), and embedding models are
     *  capped at 512 tokens. Further, 1500 characters correspond approximately to 40 lines, roughly equivalent to a
     *  small to medium-sized function or class.
     *
     * Our token length is 1024, so we can use 1500 * 1024 / 512 = 1500
     */
    val maxCharInCode: Int = MAX_CHAR_IN_CODE,
    /**
     * Our token length is 1024, so we can use 40 * 2048 / 512 = 320
     */
    val maxLineInCode: Int = MAX_LINE_IN_CODE,
    val badsmellThreshold: Map<String, Int> = BsThresholds().toThresholds(),
    val maxTokenLength: Int = MAX_TOKEN_LENGTH,
) {
    companion object {
        const val MAX_TOKEN_LENGTH: Int = 2048
        const val MAX_COMPLEXITY: Int = 1000
        const val MAX_PROJECT_TYPED_COMPLETION_SIZE: Int = 1000
        const val MAX_FILE_SIZE: Int = 1024 * 64
        const val MAX_LINE_IN_CODE: Int = 320
        const val MAX_CHAR_IN_CODE: Int = 1500
        const val MAX_RELATED_CODE_LINE: Int = 30
    }
}
```

## BsThresholds

```kotlin
data class BsThresholds(
    val bsLongParasLength: Int = 5,
    val bsIfSwitchLength: Int = 8,
    val bsLargeLength: Int = 20,
    val bsMethodLength: Int = 30,
    val bsIfLinesLength: Int = 3,
)
```

## Others

### Complexity & FileSize Metric

| Filename                                            | Complexity | Code Lines | Size   |
|-----------------------------------------------------|------------|------------|--------|
| List.java                                           | 86         | 2387       | 75079  |
| Stream.java                                         | 53         | 2007       | 68523  |
| Property.java                                       | 34         | 1313       | 68781  |
| Commander.java                                      | 103        | 607        | 26801  |
| ConfigConverter.java                                | 241        | 1081       | 52188  |
| ConfigConverterTest.java                            | 0          | 2183       | 109774 |
| CachedGoConfigIntegrationTest.java                  | 10         | 1307       | 76313  |
| GoConfigMigrationIntegrationTest.java               | 0          | 2224       | 115253 |
| GoConfigMigratorIntegrationTest.java                | 4          | 1513       | 81410  |
| BasicCruiseConfig.java                              | 225        | 1628       | 62427  |
| JobConfig.java                                      | 118        | 565        | 23309  |
| PipelineConfig.java                                 | 147        | 998        | 36415  |
| ConfigFileFixture.java                              | 1          | 1825       | 85626  |
| MagicalGoConfigXmlLoaderTest.java                   | 2          | 4394       | 230409 |
| PipelineSqlMapDao.java                              | 120        | 972        | 45329  |
| MaterialRepository.java                             | 135        | 1108       | 54763  |
| AgentServiceTest.java                               | 0          | 1543       | 75019  |
| PipelineSqlMapDaoIntegrationTest.java               | 19         | 1778       | 94116  |
| StageSqlMapDaoIntegrationTest.java                  | 26         | 1926       | 98180  |
| MaterialRepositoryIntegrationTest.java              | 10         | 1718       | 93771  |
| AgentServiceIntegrationTest.java                    | 2          | 1413       | 70354  |
| AutoTriggerDependencyResolutionTest.java            | 3          | 2099       | 93564  |
| PipelineConfigServiceIntegrationTest.java           | 2          | 1102       | 80678  |
| SwaggerSpecificationCreator.java                    | 217        | 1237       | 45240  |
| ConversionUtil.java                                 | 123        | 579        | 20911  |
| ObservationSearchQueryTest.java                     | 21         | 1930       | 76763  |
| ObservationFhirResourceProviderIntegrationTest.java | 12         | 1503       | 69780  |
| ObservationFhirResourceProviderIntegrationTest.java | 12         | 1660       | 76629  |
| BaseDao.java                                        | 190        | 1224       | 47815  |
| ImmunizationTranslatorImpl.java                     | 105        | 442        | 15772  |
| RestServiceImpl.java                                | 114        | 715        | 29624  |
| BaseDelegatingResource.java                         | 109        | 851        | 31573  |
