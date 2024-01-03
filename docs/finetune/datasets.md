---
layout: default
title: Datasets
parent: FineTune
nav_order: 98
---

## 开源数据集


### 中文代码数据集

- https://github.com/Denilah/CoLLaMA
- https://github.com/sahil280114/codealpaca

### Math

- [OpenOrca](https://huggingface.co/datasets/Open-Orca/OpenOrca)
- [zetavg/ShareGPT-Processed](https://huggingface.co/datasets/zetavg/ShareGPT-Processed)
- [MathInstruct](https://huggingface.co/datasets/TIGER-Lab/MathInstruct)

## 内部数据集

应用类：根据组织内部选择

架构类：

```yml
- repository: https://github.com/ttulka/ddd-example-ecommerce
  branch: main
  language: java
- repository: https://github.com/TNG/ArchUnit-Examples
  branch: main
  language: java
- repository: https://github.com/iluwatar/java-design-patterns
  branch: master
  language: java
```

框架类：

```yml
- repository: https://github.com/spring-projects-experimental/spring-ai
  branch: main
  language: java
- repository: https://github.com/spring-projects/spring-data-jpa
  branch: main
  language: java
```

框架示例：获取最新的 API 使用（需要结合版本）

```yml
- repository: https://github.com/spring-projects/spring-data-examples
  branch: main
  language: java
- repository: https://github.com/spring-projects/spring-security-samples
  branch: main
  language: java
```
