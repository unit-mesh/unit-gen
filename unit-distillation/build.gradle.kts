@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
    alias(libs.plugins.serialization)
    application
}

dependencies {
    implementation(libs.clikt)
    implementation(libs.serialization.json)

    implementation(libs.cf.language)
    implementation(libs.kaml)

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
    mainClass.set("cc.unitmesh.runner.DistillateKt")
}

tasks {
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "cc.unitmesh.runner.DistillateKt"))
        }
        dependencies {
            exclude(dependency("org.junit.jupiter:.*:.*"))
            exclude(dependency("org.junit:.*:.*"))
            exclude(dependency("junit:.*:.*"))
        }
    }
}
