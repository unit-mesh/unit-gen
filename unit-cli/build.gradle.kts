@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
    alias(libs.plugins.serialization)
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.unitCore)
    implementation(projects.unitPicker)

    implementation(libs.clikt)
    implementation(libs.kaml)
    implementation(libs.serialization.json)
    implementation(libs.coroutines.core)

    // Logging
    implementation(libs.logging.slf4j.api)
    implementation(libs.logging.logback.classic)

    testImplementation(kotlin("test"))

    testImplementation(libs.bundles.test)
}

tasks.test {
    useJUnitPlatform()
}


application {
    mainClass.set("cc.unitmesh.runner.MainKt")
}

tasks {
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "cc.unitmesh.runner.MainKt"))
        }
        // minimize()
        dependencies {
            exclude(dependency("org.junit.jupiter:.*:.*"))
            exclude(dependency("org.junit:.*:.*"))
            exclude(dependency("junit:.*:.*"))
        }
    }
}
