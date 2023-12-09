---
layout: default
title: Related Code Completion
parent: Instruction
nav_order: 1
permalink: /instruction/related-code-completion
---

Implement class: RelatedCodeCompletionBuilder

Logic:

```
// 1. Collect all related data structures by imports if they exist in a file tree
relatedDataStructure = []
for each import in container.Imports:
    if context.fileTree[import.Source] exists:
        dataStructures = context.fileTree[import.Source].container.DataStructures
        relatedDataStructure.append(dataStructures)

// Flatten the list of related data structures
relatedDataStructure = flatten(relatedDataStructure)

// 2. Convert all related data structures to UML
relatedCode = ""
for each dataStructure in relatedDataStructure:
    umlCode = dataStructure.toUml()
    relatedCode += umlCode + "\n"

// 3. Check with rules specified in the config
dataStructs = []
for each dataStructure in container.DataStructures:
    if hasIssue(dataStructure, context.qualityTypes):
        dataStructs.append(dataStructure)
        
// 4. Build the code completion instructions        
```

