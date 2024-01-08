<h1 align="center">UnitGen</h1>
<p align="center">
  <img src="docs/logo.svg" width="160px" height="160px" alt="UnitGen Logo">
</p>
<p align="center">
  <a href="https://github.com/unit-mesh/unit-gen/actions/workflows/build.yml">
    <img src="https://github.com/unit-mesh/unit-gen/actions/workflows/build.yml/badge.svg" alt="CI/CD" />
  </a>
  <a href="https://github.com/unit-mesh/chocolate-factory">
    <img src="https://img.shields.io/badge/powered_by-chocolate_factory-blue?logo=kotlin&logoColor=fff" alt="Powered By" />
  </a>
  <a href="https://central.sonatype.com/artifact/cc.unitmesh/unit-picker">
    <img src="https://img.shields.io/maven-central/v/cc.unitmesh/unit-picker"  alt="Maven"/>
  </a>
  <a href="https://openbayes.com/console/signup?r=phodal_uVxU">
    <img src="https://openbayes.com/img/badge-open-in-openbayes.svg" alt="Open In OpenBayes" />
  </a>
  <a href="https://openbayes.com/console/signup?r=phodal_uVxU">
    <img src="https://openbayes.com/img/badge-built-with-openbayes.svg" alt="Built with OpenBayes" />
  </a>
  <a href="https://codecov.io/gh/unit-mesh/unit-gen">
    <img src="https://codecov.io/gh/unit-mesh/unit-gen/branch/master/graph/badge.svg?token=nt22RX52DV" alt="codecov" />
  </a>
</p>

> UnitGen 是一个用于生成微调代码的数据框架 —— 直接从你的代码库中生成微调数据：代码补全、测试生成、文档生成等。

Docs: [https://gen.unitmesh.cc/](https://gen.unitmesh.cc/)

Thanks to [OpenBayes](https://openbayes.com/console/signup?r=phodal_uVxU) for providing computing resources.

Finetune Model Examples:

| name          | model download (HuggingFace)                                              | finetune Notebook                    | model download (OpenBayes)                                                          |
|---------------|---------------------------------------------------------------------------|--------------------------------------|-------------------------------------------------------------------------------------|
| DeepSeek 6.7B | [unit-mesh/autodev-coder](https://huggingface.co/unit-mesh/autodev-coder) | [finetune.ipynb](finetunes/deepseek) | [AutoDev Coder](https://openbayes.com/console/phodal/models/rCmer1KQSgp/9/overview) |

Language support by [Chapi](https://github.com/phodal/chapi)

- supported:
    - [x] Java
    - [x] Kotlin
- doing:
    - [x] TypeScript/JavaScript
    - [x] Rust
- future:
    - [ ] Go
    - [ ] Python
    - [ ] C/C++
    - [ ] C#
    - [ ] Scala

Features:

- Code context
  strategy: [Related code completion](https://gen.unitmesh.cc/instruction/related-code-completion), [Similar Code Completion](https://gen.unitmesh.cc/instruction/similar-code-completion)
- Instruction Builder type: inline, block, after block, documentation, test gen
- [Code quality](https://gen.unitmesh.cc/quality) filter and pipeline. Code smell, test smell, estimation and more.

## Architecture

Layered Architecture

![Architecture](docs/architecture.svg)

Workflow

![UnitGen Workflow](docs/workflow.svg)

### Design Philosophy

- Unique prompt. Integrated use of fine-tuning, evaluation, and tooling.
- Code quality pipeline. With estimate with code complex, bad smell, test bad smell, and more rules.
- Extendable customize quality thresholds. Custom rules, custom thresholds, custom quality type or more.

### Unique Prompt

Keep the same prompt: AutoDev <-> UnitGen <-> UnitEval

#### AutoDev prompt

AutoDev prompt template example:

    Write unit test for following code.
    
    ${context.coc}
    
    ${context.framework}
    
    ${context.related_model}
    
    ```${context.language}
    ${context.selection}
    ```

#### Unit Picker prompt

Unit Picker prompt should keep the same structure as the AutoDev prompt. Prompt example:

```kotlin
Instruction(
    instruction = "Complete ${it.language} code, return rest code, no explaining",
    output = it.output,
    input = """
    |```${it.language}
    |${it.relatedCode}
    |```
    |
    |Code:
    |```${it.language}
    |${it.beforeCursor}
    |```""".trimMargin()
)
```

#### UnitGen prompt

UnitGen prompt should keep the same structure as the AutoDev prompt. Prompt example:

    Complete ${language} code, return rest code, no explaining
    
    ```${language}
    ${relatedCode}
    ```
    
    Code:
    ```${language}
    ${beforeCursor}
    ```

### Code quality pipeline

![Code Quality Workflow](docs/workflow.svg)

### Extendable customize quality thresholds

Optional quality type:

```kotlin
enum class CodeQualityType {
    BadSmell,
    TestBadSmell,
    JavaController,
    JavaRepository,
    JavaService,
}
```

Custom thresholds' config:

```kotlin
data class BsThresholds(
    val bsLongParasLength: Int = 5,
    val bsIfSwitchLength: Int = 8,
    val bsLargeLength: Int = 20,
    val bsMethodLength: Int = 30,
    val bsIfLinesLength: Int = 3,
)
```

Custom rules:

```kotlin
val apis = apiAnalyser.toContainerServices()
val ruleset = RuleSet(
    RuleType.SQL_SMELL,
    "normal",
    UnknownColumnSizeRule(),
    LimitTableNameLengthRule()
    // more rules
)

val issues = WebApiRuleVisitor(apis).visitor(listOf(ruleset))
// if issues are not empty, then the code has bad smell
```

## Quick Start

for examples, see: [examples](https://github.com/unit-mesh/unit-gen/tree/master/examples) folder

### use CLI

see in [config-examples](https://github.com/unit-mesh/unit-gen/tree/master/examples/config-examples/)

download the latest version from [GitHub Release](https://github.com/unit-mesh/unit-gen/releases)

#### Generate Instructions

1. config project by `processor.yml`
2. run picker: `java -jar unit-gen.jar`

### use Java API

see in [config-example](examples/project-example/)

1.add dependency

```groovy
dependencies {
    implementation("cc.unitmesh:unit-picker:0.1.5")
    implementation("cc.unitmesh:code-quality:0.1.5")
}
```

2.config the `unit-gen.yml` file and `connection.yml`

3.write code

```java
public class App {
    public static void main(String[] args) {
        List<InstructionType> builderTypes = new ArrayList<>();
        builderTypes.add(InstructionType.RELATED_CODE_COMPLETION);

        List<CodeQualityType> codeQualityTypes = new ArrayList<>();
        codeQualityTypes.add(CodeQualityType.BadSmell);
        codeQualityTypes.add(CodeQualityType.JavaService);

        PickerOption pickerOption = new PickerOption(
                "https://github.com/unit-mesh/unit-gen-testing", "master", "java",
                ".", builderTypes, codeQualityTypes, new BuilderConfig()
        );

        SimpleCodePicker simpleCodePicker = new SimpleCodePicker(pickerOption);
        List<Instruction> output = simpleCodePicker.blockingExecute();

        // handle output in here
    }
} 
```

## Thanks to

- abstract syntax tree: [Chapi](https://github.com/phodal/chapi). Used features: multiple language to same data
  structure.
- legacy system analysis: [Coca](https://github.com/phodal/coca). Inspired: Bad Smell, Test Bad Smell
- architecture governance tool: [ArchGuard](https://github.com/archguard/archguard).
  Used features: Estimation, Rule Lint (API, SQL)
- code database [CodeDB](https://github.com/archguard/codedb). Used features: Code analysis pipeline

## LICENSE

This code is distributed under the MPL 2.0 license. See `LICENSE` in this directory.
