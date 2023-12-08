@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.serialization)
}

dependencies {
    implementation(libs.serialization.json)

    implementation(libs.chapi.domain)
    implementation(libs.chapi.java)
    implementation(libs.chapi.kotlin)

    implementation(libs.archguard.scanner.core)
    implementation(libs.archguard.analyser.estimate)

    implementation(libs.archguard.rule.sql)
    implementation(libs.archguard.rule.webapi)

    // checkout
    implementation(libs.codedb.checkout)

    // Logging
    implementation(libs.logging.slf4j.api)
    implementation(libs.logging.logback.classic)

    testImplementation(kotlin("test"))

    testImplementation(libs.bundles.test)
}

tasks.test {
    useJUnitPlatform()
}
