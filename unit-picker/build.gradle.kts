@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.serialization)
}

dependencies {
    implementation(projects.unitCore)
    implementation(projects.codeQuality)

    implementation(libs.clikt)
    implementation(libs.serialization.json)
    implementation(libs.coroutines.core)
    implementation(libs.kotlin.reflect)

    implementation(libs.chapi.domain)
    implementation(libs.chapi.java)
    implementation(libs.chapi.kotlin)
    implementation(libs.chapi.typescript)
    implementation(libs.chapi.rust)

    implementation(libs.archguard.scanner.core)
    implementation(libs.archguard.analyser.estimate)
    implementation(libs.archguard.analyser.sca)

    // checkout
    implementation(libs.codedb.checkout)
    implementation(libs.codedb.action.toolkit)

    // tokenization
    implementation(libs.jtokkit)

    // Logging
    implementation(libs.logging.slf4j.api)
    implementation(libs.logging.logback.classic)

    testImplementation(kotlin("test"))

    testImplementation(libs.bundles.test)
}

tasks.test {
    useJUnitPlatform()
}
