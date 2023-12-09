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
    implementation("cc.unitmesh:unit-picker:0.1.2")
    implementation("cc.unitmesh:code-quality:0.1.2")
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

## LICENSE

This code is distributed under the MPL 2.0 license. See `LICENSE` in this directory.
