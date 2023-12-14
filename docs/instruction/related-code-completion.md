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

temp dir:

```json
{
  "language": "java",
  "beforeCursor": "package com.example;\n\nimport com.example.admin.common.AdminCriteria;\nimport com.example.config.ResetDbListener;\nimport com.example.domain.iam.auth.model.Authorize;\nimport com.example.domain.iam.auth.service.AuthorizeService;\nimport com.example.domain.iam.user.model.User;\nimport com.example.domain.iam.user.repository.UserRepository;\nimport io.restassured.RestAssured;\nimport org.junit.jupiter.api.BeforeEach;\nimport org.springframework.beans.factory.annotation.Autowired;\nimport org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;\nimport org.springframework.boot.test.context.SpringBootTest;\nimport org.springframework.boot.web.server.LocalServerPort;\nimport org.springframework.test.context.ActiveProfiles;\nimport org.springframework.test.context.TestExecutionListeners;\nimport org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;\nimport org.springframework.test.context.support.DependencyInjectionTestExecutionListener;\n\nimport static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;\n\n@SpringBootTest(webEnvironment = RANDOM_PORT, classes = {AdminTestApplication.class})\n@TestExecutionListeners({\n        DependencyInjectionTestExecutionListener.class,\n        ResetDbListener.class,\n        SqlScriptsTestExecutionListener.class,\n})\n@AutoConfigureMockMvc\n@ActiveProfiles(\"test\")\npublic abstract class TestBase {\n\n    @LocalServerPort\n    private int port;\n\n    @Autowired\n    private UserRepository userRepository;\n\n    @Autowired\n    private AuthorizeService authorizeService;\n\n    @BeforeEach\n    public void setUp() {",
  "relatedCode": "// class AdminCriteria {\n// \n//    + ofName(name: String): Specification<User>\n//  }\n// \n// class ResetDbListener {\n// \n//    'getter/setter: getOrder\n// \n//    + beforeTestMethod(testContext: TestContext): void\n//    + afterTestMethod(testContext: TestContext): void\n//  }\n// \n// class Authorize {\n//    : String\n//    : String\n//    : User\n//    : Long\n// \n//    'getter/setter: setExpire\n// \n//    + build(userId: String, role: User.Role): Authorize\n//  }\n// \n// class AuthorizeService {\n//    : AuthorizeRepository\n//    : BCryptPasswordEncoder\n// \n//    'getter/setter: getOperator\n// \n//    + create(user: User, password: String): Authorize\n//    + delete(id: String): void\n//  }\n// \n// class Status {\n//    : String\n//    : String\n//    : String\n//    : String\n//    : Instant\n//    : Instant\n//    : Role\n//    : Status\n// \n//    + build(name: String, email: String, password: String): User\n//  }\n// \n// class UserRepository {\n// \n//  }\n// ",
  "output": "        System.out.println(\"port:\" + port);\n\n        RestAssured.port = port;\n        RestAssured.basePath = \"/\";\n        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();\n    }"
}
```

related code:

```plantuml
// class AdminCriteria {
// 
//    + ofName(name: String): Specification<User>
//  }
// 
// class ResetDbListener {
// 
//    'getter/setter: getOrder
// 
//    + beforeTestMethod(testContext: TestContext): void
//    + afterTestMethod(testContext: TestContext): void
//  }
// 
// class Authorize {
//    id: String
//    userId: String
//    role: User
//    expire: Long
// 
//    'getter/setter: setExpire
// 
//    + build(userId: String, role: User.Role): Authorize
//  }
// 
// class AuthorizeService {
//    repository: AuthorizeRepository
//    bCryptPasswordEncoder: BCryptPasswordEncoder
// 
//    'getter/setter: getOperator
// 
//    + create(user: User, password: String): Authorize
//    + delete(id: String): void
//  }
// 
// class Status {
//    id: String
//    name: String
//    email: String
//    password: String
//    createdAt: Instant
//    updatedAt: Instant
//    role: Role
//    status: Status
// 
//    + build(name: String, email: String, password: String): User
//  }
// 
// class UserRepository {
// 
//  }
// 
```

example:

```json
{
  "instruction": "Complete java code, return rest code, no explaining",
  "input": "```java\n\n```\n\nCode:\n```java\npackage com.example;\n\nimport org.springframework.boot.SpringApplication;\nimport org.springframework.boot.autoconfigure.SpringBootApplication;\nimport org.springframework.boot.context.properties.EnableConfigurationProperties;\n\n@SpringBootApplication\n@EnableConfigurationProperties\npublic class AdminTestApplication {\n\n    public static void main(String[] args) {\n```",
  "output": "        SpringApplication.run(AdminTestApplication.class, args);\n    }"
}
```