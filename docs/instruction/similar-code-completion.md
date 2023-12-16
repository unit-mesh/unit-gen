---
layout: default
title: Similar Code Completion
parent: Instruction Builder
nav_order: 2
permalink: /instruction/similar-code-builder
---

# Similar Code Completion

Implement class: SimilarChunksCompletionBuilder

## Core Logic

```
function calculate(text: String, canonicalName: String) -> SimilarChunkContext:
    Split `text` into `lines` by line breaks
    Take the last `snippetLength` lines from `lines` and join them into `beforeCursor` string
    
    Get a list of `canonicalNames` from the keys of `fileTree`
    Calculate the similarity between `canonicalName` and `canonicalNames`
    Sort the results in descending order and take the top `maxRelevantFiles` paths as `relatedCodePath`
    
    Log the value of `relatedCodePath`
    
    Split `beforeCursor` into `chunks`
    Get all related code chunks from `fileTree` using `relatedCodePath`
    Join the chunks into `allRelatedChunks` string
    
    Log the value of `allRelatedChunks`
    
    Calculate the similarity score between each chunk in `allRelatedChunks` and `chunks`
    Sort the results in descending order and take the top `maxRelevantFiles` chunks as `similarChunks`
    
    If the size of `similarChunks` is greater than 3, take the first 3 chunks, otherwise, take all chunks
    
    Create a `SimilarChunkContext` object with language set to "java",
    `relatedCodePath` set to `relatedCodePath`, and `similarChunks` set to `similarChunks`
    
    Return the `SimilarChunkContext` object
```

## Template data

```json
{
  "language": "java",
  "beforeCursor": "package com.example.config;\n\nimport org.dbunit.DatabaseUnitException;\nimport org.dbunit.database.DatabaseConfig;\nimport org.dbunit.database.DatabaseConnection;\nimport org.dbunit.database.IDatabaseConnection;\nimport org.dbunit.database.QueryDataSet;\nimport org.dbunit.dataset.IDataSet;\nimport org.dbunit.dataset.xml.FlatXmlDataSet;\nimport org.dbunit.dataset.xml.FlatXmlDataSetBuilder;\nimport org.dbunit.operation.DatabaseOperation;\nimport org.springframework.beans.factory.annotation.Autowired;\nimport org.springframework.jdbc.datasource.DataSourceUtils;\nimport org.springframework.stereotype.Service;\n\nimport javax.sql.DataSource;\nimport java.io.File;\nimport java.io.FileInputStream;\nimport java.io.FileNotFoundException;\nimport java.io.FileWriter;\nimport java.sql.Connection;\nimport java.sql.DatabaseMetaData;\nimport java.sql.ResultSet;\nimport java.sql.SQLException;\nimport java.util.ArrayList;\nimport java.util.List;\n\n/**\n * 这个类的目的是通过每次备份系统初始化的数据（这些初始化数据可能来自 flyway），\n * 来实现每次测试的数据一致的目的。\n */\n\n@Service\npublic class ResetDbService {\n\n    public static final String ROOT_URL = \"build/resources/test/\";\n    private static IDatabaseConnection conn;\n\n    @Autowired\n    private DataSource dataSource;\n    private File tempFile;\n\n    public void backUp() throws Exception {\n        this.getConnection();\n        this.backupCustom(tables());\n    }\n\n    public void rollback() throws Exception {\n        this.reset();\n        this.closeConnection();\n    }\n\n    List<String> tables() throws SQLException {\n        Connection connection = dataSource.getConnection();\n        DatabaseMetaData metaData = connection.getMetaData();\n        ResultSet tables = metaData.getTables(null, null, null, new String[]{\"TABLE\"});\n        ArrayList<String> tableNames = new ArrayList<>();\n        while (tables.next()) {\n            String tableName = tables.getString(\"TABLE_NAME\");\n            tableNames.add(tableName);\n        }\n\n        connection.close();\n        return tableNames;\n    }\n\n    protected void backupCustom(List<String> tableName) {",
  "afterCursor": "        try {\n            QueryDataSet qds = getQueryDataSet();\n            for (String str : tableName) {\n                qds.addTable(str);\n            }\n\n            conn.getConfig().setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN , \"`?`\");\n\n            tempFile = new File(ROOT_URL + \"temp.xml\");\n            FlatXmlDataSet.write(qds, new FileWriter(tempFile), \"UTF-8\");\n        } catch (Exception e) {\n            e.printStackTrace();\n        }\n    }",
  "similarChunks": [
    "\n\npublic class ResetDbListener extends AbstractTestExecutionListener {\n\n    @Override\n    public int getOrder() {\n        return 4500;\n    }\n\n    @Override\n    public void beforeTestMethod(TestContext testContext) throws Exception {\n        ResetDbService resetDbService =\n                testContext.getApplicationContext().getBean(ResetDbService.class);\n        resetDbService.backUp();\n    }\n\n    @Override\n    public void afterTestMethod(TestContext testContext) throws Exception {\n        ResetDbService resetDbService =\n                testContext.getApplicationContext().getBean(ResetDbService.class);\n        resetDbService.rollback();\n    }\n}\n"
  ],
  "output": "        try {\n            QueryDataSet qds = getQueryDataSet();\n            for (String str : tableName) {\n                qds.addTable(str);\n            }\n\n            conn.getConfig().setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN , \"`?`\");\n\n            tempFile = new File(ROOT_URL + \"temp.xml\");\n            FlatXmlDataSet.write(qds, new FileWriter(tempFile), \"UTF-8\");\n        } catch (Exception e) {\n            e.printStackTrace();\n        }\n    }"
}
```

### BeforeCursor code

```java
package com.example.config;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 这个类的目的是通过每次备份系统初始化的数据（这些初始化数据可能来自 flyway），
 * 来实现每次测试的数据一致的目的。
 */

@Service
public class ResetDbService {

    public static final String ROOT_URL = "build/resources/test/";
    private static IDatabaseConnection conn;

    @Autowired
    private DataSource dataSource;
    private File tempFile;

    public void backUp() throws Exception {
        this.getConnection();
        this.backupCustom(tables());
    }

    public void rollback() throws Exception {
        this.reset();
        this.closeConnection();
    }

    List<String> tables() throws SQLException {
        Connection connection = dataSource.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});
        ArrayList<String> tableNames = new ArrayList<>();
        while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            tableNames.add(tableName);
        }

        connection.close();
        return tableNames;
    }

    protected void backupCustom(List<String> tableName) {
```

### similarChunks

```java
public class ResetDbListener extends AbstractTestExecutionListener {

    @Override
    public int getOrder() {
        return 4500;
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        ResetDbService resetDbService =
                testContext.getApplicationContext().getBean(ResetDbService.class);
        resetDbService.backUp();
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        ResetDbService resetDbService =
                testContext.getApplicationContext().getBean(ResetDbService.class);
        resetDbService.rollback();
    }
}
```

