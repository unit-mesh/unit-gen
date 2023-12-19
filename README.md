<h1 align="center">Unit Eval</h1>

<p align="center">
  <a href="https://github.com/unit-mesh/unit-eval/actions/workflows/build.yml">
    <img src="https://github.com/unit-mesh/unit-eval/actions/workflows/build.yml/badge.svg" alt="CI/CD" />
  </a>
  <a href="https://github.com/unit-mesh/chocolate-factory">
    <img src="https://img.shields.io/badge/powered_by-chocolate_factory-blue?logo=kotlin&logoColor=fff" alt="Powered By" />
  </a>
  <a href="https://central.sonatype.com/artifact/cc.unitmesh/unit-picker">
    <img src="https://img.shields.io/maven-central/v/cc.unitmesh/unit-picker"  alt="Maven"/>
  </a>
</p>

> LLM benchmark/evaluation tools with fine-tuning data engineering, specifically tailored for Unit Mesh tools such as
> AutoDev, Studio B3, and DevOps Genius.

Docs: [https://eval.unitmesh.cc/](https://eval.unitmesh.cc/)

Thanks to [OpenBayes](https://openbayes.com/console/signup?r=phodal_uVxU) for providing computing resources.

Features:

- Code context strategy: [Related code completion](https://eval.unitmesh.cc/instruction/related-code-completion), [Similar Code Completion](https://eval.unitmesh.cc/instruction/similar-code-completion)
- Completion type: inline, block, after block
- [Code quality](https://eval.unitmesh.cc/quality) filter and pipeline. Code smell, test smell, estimation nd more.

## Design Philosophy

- Unique prompt. Integrated use of fine-tuning, evaluation, and tooling.
- Code quality pipeline. With estimate with code complex, bad smell, test bad smell, and more rules.
- Extendable customize quality thresholds. Custom rules, custom thresholds, custom quality type or more.

### Unique Prompt

![Unit Eval Overview](https://unitmesh.cc/uniteval/overview.png)

Keep the same prompt: AutoDev <-> Unit Picker <-> Unit Eval

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

#### Unit Eval prompt

Unit Eval prompt should keep the same structure as the AutoDev prompt. Prompt example:

    Complete ${language} code, return rest code, no explaining
    
    ```${language}
    ${relatedCode}
    ```
    
    Code:
    ```${language}
    ${beforeCursor}
    ```

### Code quality pipeline

Before Check:

- FileSize: 64k
- Complexity: 1000

![Code Quality Workflow](https://unitmesh.cc/uniteval/code-quality-workflow.png)

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

for examples, see: [examples](https://github.com/unit-mesh/unit-eval/tree/master/examples) folder

### use CLI

see in [config-examples](https://github.com/unit-mesh/unit-eval/tree/master/examples/config-examples/)

download the latest version from [GitHub Release](https://github.com/unit-mesh/unit-eval/releases)

#### Step 1. Generate Instructions

1. config project by `processor.yml`
2. run picker: `java -jar unit-cli.jar`

### Step 2. run Evaluate CLI (Optional)

1.config the `unit-eval.yml` file and `connection.yml`

2.run eval: `java -jar unit-eval.jar`

PS：Connection config: [https://framework.unitmesh.cc/prompt-script/connection-config](https://framework.unitmesh.cc/prompt-script/connection-config)

### use Java API

see in [config-example](examples/project-example/)

1.add dependency

```groovy
dependencies {
    implementation("cc.unitmesh:unit-picker:0.1.5")
    implementation("cc.unitmesh:code-quality:0.1.5")
}
```

2.config the `unit-eval.yml` file and `connection.yml`

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
            "https://github.com/unit-mesh/unit-eval-testing", "master", "java",
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
