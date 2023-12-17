---
layout: default
title: Quick Start
nav_order: 2
permalink: /quick-start
---

# Quick Start

for examples, see: [examples](https://github.com/unit-mesh/unit-eval/tree/master/examples) folder

## use CLI

see in [config-examples](https://github.com/unit-mesh/unit-eval/tree/master/examples/config-examples/)

download the latest version from [GitHub Release](https://github.com/unit-mesh/unit-eval/releases)

### Step 1. Generate Instructions

1. config project by `processor.yml`
2. run picker: `java -jar unit-cli.jar`

### Step 2. run Evaluate CLI (Optional)

1.config the `unit-eval.yml` file and `connection.yml`

2.run eval: `java -jar unit-eval.jar`

PS：Connection config: [https://framework.unitmesh.cc/prompt-script/connection-config](https://framework.unitmesh.cc/prompt-script/connection-config)

## use Java API

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

