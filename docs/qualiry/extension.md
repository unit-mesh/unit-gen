---
layout: default
title: Quality
nav_order: 2
has_children: true
permalink: /quality
---

# Quality

Overflow:

![Code Quality Workflow](https://unitmesh.cc/uniteval/code-quality-workflow.png)

## QualityAnalyser

```kotlin
interface QualityAnalyser {
    fun analysis(nodes: List<CodeDataStruct>): List<Issue>

    companion object {
        /**
         * Creates a list of QualityAnalyser objects based on the given list of CodeQualityType.
         *
         * @param types The list of CodeQualityType to create QualityAnalyser objects for.
         * @param thresholds The map of thresholds for each CodeQualityType. Defaults to an empty map if not provided.
         * @return A list of QualityAnalyser objects corresponding to the given CodeQualityType.
         */
        fun create(types: List<CodeQualityType>, thresholds: Map<String, Int> = mapOf()): List<QualityAnalyser> {
            return types.map { type ->
                when (type) {
                    CodeQualityType.BadSmell -> BadsmellAnalyser(thresholds)
                    CodeQualityType.TestBadSmell -> TestBadsmellAnalyser(thresholds)
                    CodeQualityType.JavaController -> JavaRepositoryAnalyser(thresholds)
                    CodeQualityType.JavaRepository -> JavaServiceAnalyser(thresholds)
                    CodeQualityType.JavaService -> JavaControllerAnalyser(thresholds)
                }
            }
        }
    }
}
```

## AST Parser

> [抽象语法树 (Abstract syntax tree)](https://en.wikipedia.org/wiki/Abstract_syntax_tree) is a data structure used in
> computer science to represent the structure of a program or code snippet. It is a tree representation of the abstract
> syntactic structure of text (often source code) written in a formal language. Each node of the tree denotes a construct
> occurring in the text. It is sometimes called just a syntax tree.

UnitEval use [Chapi](https://github.com/phodal/chapi) as language parser, which will convert source code to AST. For
examples:

```java
package adapters.outbound.persistence.blog;

public class BlogPO implements PersistenceObject<Blog> {
    @Override
    public Blog toDomainModel() {

    }
}
```

Output:

```json
{
  "Imports": [],
  "Implements": [
    "PersistenceObject<Blog>"
  ],
  "NodeName": "BlogPO",
  "Extend": "",
  "Type": "CLASS",
  "FilePath": "",
  "InOutProperties": [],
  "Functions": [
    {
      "IsConstructor": false,
      "InnerFunctions": [],
      "Position": {
        "StartLine": 6,
        "StartLinePosition": 133,
        "StopLine": 8,
        "StopLinePosition": 145
      },
      "Package": "",
      "Name": "toDomainModel",
      "MultipleReturns": [],
      "Annotations": [
        {
          "Name": "Override",
          "KeyValues": []
        }
      ],
      "Extension": {},
      "Override": false,
      "extensionMap": {},
      "Parameters": [],
      "InnerStructures": [],
      "ReturnType": "Blog",
      "Modifiers": [],
      "FunctionCalls": []
    }
  ],
  "Annotations": [],
  "Extension": {},
  "Parameters": [],
  "Fields": [],
  "MultipleExtend": [],
  "InnerStructures": [],
  "Package": "adapters.outbound.persistence.blog",
  "FunctionCalls": []
}
```

## ArchGuard Rule

Use examples:

```kotlin
class JavaControllerAnalyser(thresholds: Map<String, Int> = mapOf()) : QualityAnalyser {
    private val webApiRuleSetProvider = WebApiRuleSetProvider()

    override fun analysis(nodes: List<CodeDataStruct>): List<Issue> {
        val apiAnalyser = JavaApiAnalyser()

        nodes.forEach { data ->
            apiAnalyser.analysisByNode(data, "")
        }
        val services = apiAnalyser.toContainerServices()
        return WebApiRuleVisitor(services).visitor(listOf(webApiRuleSetProvider.get()))
    }
}
```

