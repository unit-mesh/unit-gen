---
layout: default
title: Quick Start
nav_order: 2
permalink: /quick-start
---

# Quick Start

for examples, see: [examples](https://github.com/unit-mesh/unit-gen/tree/master/examples) folder

## use in IDE

1. config project by `processor.yml`
2. run `PickerKt` 

or run in `unit-cli/src/main/kotlin/cc/unitmesh/runner/Picker.kt`

## use CLI

### use CLI

see in [config-examples](https://github.com/unit-mesh/unit-gen/tree/master/examples/config-examples/)

download the latest version from [GitHub Release](https://github.com/unit-mesh/unit-gen/releases)

#### Generate Instructions

1. config project by `processor.yml`
2. run picker: `java -jar unit-gen.jar`

Processor.yml examples:

```yaml
projects:
  - repository: https://github.com/domain-driven-design/ddd-lite-example
    branch: main
    language: java
  - repository: https://github.com/unit-mesh/unit-gen-testing
    branch: main
    language: java

instructionConfig:
  mergeInput: true # if the LLM don't support separate input, you can set it to true, will merge input to instruction.  
```

## use Java API

see in [config-example](examples/project-example/)

1.add dependency

```groovy
dependencies {
    implementation("cc.unitmesh:unit-picker:0.1.7")
    implementation("cc.unitmesh:code-quality:0.1.7")
}
```

2.write code
```java
public class App {
    public static void main(String[] args) {
        List<CodeContextStrategy> instructionTypes = new ArrayList<>();
        instructionTypes.add(CodeContextStrategy.RELATED_CODE);

        // config your code quality types
        List<CodeQualityType> codeQualityTypes = new ArrayList<>();
        codeQualityTypes.add(CodeQualityType.BadSmell);
        codeQualityTypes.add(CodeQualityType.JavaService);

        BuilderConfig builderConfig = new BuilderConfig();

        List<CompletionBuilderType> completionTypes = new ArrayList<>();
        completionTypes.add(CompletionBuilderType.IN_BLOCK_COMPLETION);
        completionTypes.add(CompletionBuilderType.AFTER_BLOCK_COMPLETION);

        PickerOption pickerOption = new PickerOption(
                "https://github.com/unit-mesh/unit-gen-testing", "master", "java",
                ".", instructionTypes, completionTypes, codeQualityTypes, builderConfig
        );

        SimpleCodePicker simpleCodePicker = new SimpleCodePicker(pickerOption);
        List<Instruction> output = simpleCodePicker.blockingExecute();

        // handle output
        System.out.println(output);
    }
}
```
