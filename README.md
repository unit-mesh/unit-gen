# UnitEval

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

### Code Picker

> Code Picker is a tool that can pick the code from the real project, will generate the code unit, and then it will be
> the datasets to Fine-tuning the model.

- [ ] by History analysis for incremental learning
    - [ ] Git history parser
    - [ ] Incremental generate
- [ ] Code quality analysis
    - [ ] Code quality metrics
    - [ ] ArchGuard CodeDB metrics: [CodeDB](https://github.com/archguard/codedb)
- [ ] AST parser by [Chapi](https://github.com/phodal/chapi)
- [ ] Good code 
    - [x] Test BadSmell from [Coca](https://github.com/phodal/coca)
    - [ ] BadSmell from [Coca](https://github.com/phodal/coca)
    - [ ] Estimation from [ArchGuard](https://github.com/archguard/archguard)

### Code Eval

> Code Eval is a tool that can evaluate the code quality of the AI code.

- [ ] Code eval
    - [ ] Auto LLM Test by Chocolate Factory
- [ ] Config by YAML

## LICENSE

This code is distributed under the MPL 2.0 license. See `LICENSE` in this directory.
