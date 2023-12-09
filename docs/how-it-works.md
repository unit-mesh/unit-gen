---
layout: default
title: How it works!
nav_order: 99
permalink: /how-it-works
---

# How it works?

![Unit Eval Overview](https://unitmesh.cc/uniteval/overview.png)

## Module

### Code Quality

![Code Quality Workflow](https://unitmesh.cc/uniteval/code-quality-workflow.png)

- [x] Test BadSmell from [Coca](https://github.com/phodal/coca)
- [ ] BadSmell from [Coca](https://github.com/phodal/coca)
    - [x] Long Parameters, Long Method, LARGE_CLASS
    - [ ] Todos: Complex-if, Switch logic
- [x] Estimation from [ArchGuard](https://github.com/archguard/archguard)
- [ ] MVC architecture Governance: [Rule Linter](https://archguard.org/governance)
    - [x] Controller -> ArchGuard [HTTP API Rule](https://archguard.org/governance/web-api)
    - [x] Service -> Service Rule + Bad Smell
    - [x] Repository -> ArchGuard [SQL Rule](https://archguard.org/governance/sql)
    - [ ] Model -> Model Rule
    - [ ] Exception -> Exception Rule
    - [ ] Security -> Security Rule

### Code Picker

> Code Picker is a tool that can pick the code from the real project, will generate the code unit, and then it will be
> the datasets to Fine-tuning the model.

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
- [x] Auto LLM Test by Chocolate Factory
- [ ] Eval Results
- Language support by TreeSitter
    - spike C/C++ ?

