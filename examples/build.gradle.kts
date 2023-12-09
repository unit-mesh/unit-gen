plugins {
    application
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")

    implementation("cc.unitmesh:unit-picker:0.1.1")
    implementation("cc.unitmesh:code-quality:0.1.1")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("cc.unitmesh.example.App")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

