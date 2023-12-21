---
layout: default
title: Rule Extension
parent: Quality
nav_order: 1
permalink: /quality/rule
---

## Rule Example

```kotlin
import org.archguard.rule.core.IssueEmit
import org.archguard.rule.core.IssuePosition
import org.archguard.rule.core.RuleContext
import org.archguard.rule.core.Severity
import org.archguard.linter.rule.webapi.WebApiRule
import org.archguard.scanner.core.sourcecode.ContainerSupply

class NoCrudEndRule: WebApiRule() {
    init {
        this.id = "no-crud-end"
        this.name = "NoCrudEndRule"
        this.key = this.javaClass.name
        this.description = "URLs should not end with action. Incorrect: `/api/book/get`, correct: `GET /api/book`."
        this.severity = Severity.WARN
    }

    override fun visitResource(resource: ContainerSupply, context: RuleContext, callback: IssueEmit) {
        val split = resource.sourceUrl.split("/")
        if(CRUD.contains(split.last().lowercase())) {
            callback(this, IssuePosition())
        }
    }
}
```


Refs：

- [https://archguard.org/custom/custom-rule](https://archguard.org/custom/custom-rule)
- [https://archguard.org/governance](https://archguard.org/governance)


