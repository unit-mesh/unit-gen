# UnitEval

[![CI](https://github.com/unit-mesh/unit-eval/actions/workflows/build.yml/badge.svg)](https://github.com/unit-mesh/unit-eval/actions/workflows/build.yml)

> Leveraging LLM benchmark/evaluation tools with fine-tuning data engineering specifically tailored for Unit Mesh tools
> such as AutoDev, Studio B3, and DevOps Genius. The brilliance lies in the UnitEval functionality, which automatically
> selects and evaluates code within real projects.

This repository designs evaluate the code quality of the AI code, which will use
with [AutoDev](https://github.com/unit-mesh/auto-dev) IDE plugins.
We use [Chocolate Factory](https://github.com/unit-mesh/chocolate-factory) to build the prompt engine.

Related issue in Unit Mesh: Chocolate Factory [#9](https://github.com/unit-mesh/chocolate-factory/issues/9) and
AutoDev [#54](https://github.com/unit-mesh/auto-dev/issues/56)

## How it works?

![Unit Eval Overview](https://unitmesh.cc/uniteval/overview.png)

## Module

### Code Quality

- [x] Test BadSmell from [Coca](https://github.com/phodal/coca)
- [ ] BadSmell from [Coca](https://github.com/phodal/coca)
    - [x] Long Parameters, Long Method, LARGE_CLASS
    - [ ] Todos: Complex-if, Switch logic
- [x] Estimation from [ArchGuard](https://github.com/archguard/archguard)
- [ ] MVC architecture Governance: [Rule Linter](https://archguard.org/governance)
    - [x] Controller -> ArchGuard API Rule + Bad Smell
    - [x] Service -> Service Rule + Bad Smell
    - [ ] Repository -> HTTP API Rule
    - [ ] Model -> Model Rule
    - [ ] Exception -> Exception Rule
    - [ ] Security -> Security Rule

### Code Picker

> Code Picker is a tool that can pick the code from the real project, will generate the code unit, and then it will be
> the datasets to Fine-tuning the model.

![Code Quality Workflow](https://unitmesh.cc/uniteval/code-quality-workflow.png)

- [ ] Basic Instruction
    - [x] Related Code builder
    - [ ] InBlock Code builder
    - [ ] AfterBlock Code builder
- [x] Filter by Code Quality
- [x] AST parser by [Chapi](https://github.com/phodal/chapi)
- [ ] by History analysis for incremental learning
- [ ] Language support by [Chapi](https://github.com/phodal/chapi)
    - [x] Java
    - [ ] Kotlin
    - [ ] TypeScript/JavaScript
    - [ ] Python
    - [ ] Golang

### Code Eval

> Code Eval is a tool that can evaluate the code quality of the AI code.

- [x] Code eval
    - [x] EvalScript based on PromptScript
- [ ] Auto LLM Test by Chocolate Factory
- Language support by TreeSitter
    - spike C/C++ ?

## LICENSE

This code is distributed under the MPL 2.0 license. See `LICENSE` in this directory.
