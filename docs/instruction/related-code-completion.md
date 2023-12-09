---
layout: default
title: Related Code Completion
parent: Instruction
nav_order: 1
permalink: /instruction/related-code-completion
---

The code completion is based on the following:

1. Collect all related data structures: This step collects all related data structures by checking the imports in the
   file tree. The data structures are extracted from the containers associated with the source files specified in
   container.Imports.
2. Convert to UML: All related data structures are converted to UML format code.
3. Check rules: The function applies the rules specified in the configuration to filter the data structures that have
   issues.
4. Build the code completion instructions: For each data structure with issues, the function iterates through its
   associated functions. It extracts the code before and after the cursor position based on the function's position in
   the code. Then, it creates a RelatedCodeCompletionIns object with the related code, language type, code before the
   cursor, and code after the cursor. The object is added to the list of code completion instructions.
5. Return the code completion instructions: The function returns the list of code completion instructions that have been
   built.
