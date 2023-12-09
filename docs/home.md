---
layout: default
title: Home
description: Chocolate Factory 是一款开源的 AI Agent 开发引擎/数据框架（Toolkit），旨在帮助您轻松打造强大的 LLM 助手。无论您是需要生成前端页面、后端 API、SQL 图表，还是测试用例数据，Chocolate Factory 都能满足您的需求。
nav_order: 1
permalink: /
---

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

> Leveraging LLM benchmark/evaluation tools with fine-tuning data engineering specifically tailored for Unit Mesh tools
> such as AutoDev, Studio B3, and DevOps Genius. The brilliance lies in the UnitEval functionality, which automatically
> selects and evaluates code within real projects.

![Unit Eval Overview](https://unitmesh.cc/uniteval/overview.png)

Based on:

- abstract syntax tree: [Chapi](https://github.com/phodal/chapi). Used features: multiple language to same data
  structure.
- legacy system analysis: [Coca](https://github.com/phodal/coca). Inspired: Bad Smell, Test Bad Smell
- architecture governance tool: [ArchGuard](https://github.com/archguard/archguard).
  Used features: Estimation, Rule Lint (API, SQL)
- code database [CodeDB](https://github.com/archguard/codedb). Used features: Code analysis pipeline

**Features**:

- Integrated use of fine-tuning, evaluation, and tooling.
- High-quality code pipeline construction.
- Customizable quality evaluation metrics.
