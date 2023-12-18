plugins {
    application
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("cc.unitmesh:unit-picker:0.1.7")
    implementation("cc.unitmesh:code-quality:0.1.7")
}

application {
    mainClass.set("cc.unitmesh.example.App")
}
