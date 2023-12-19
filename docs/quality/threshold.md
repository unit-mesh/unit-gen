---
layout: default
title: Quality threshold
parent: Quality
nav_order: 1
permalink: /quality/threshold
---

Default threshold:

- File Size: 64kb, about 640 ~ 1280 lines, maybe some comments, like chinese
- Complexity: 100,

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

## Complexity & FileSize Metric

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
