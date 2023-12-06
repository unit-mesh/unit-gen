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
    implementation(projects.codeQuality)

    implementation(libs.clikt)
    implementation(libs.serialization.json)
    implementation(libs.coroutines.core)

    implementation(libs.chapi.domain)
    implementation(libs.chapi.java)
    implementation(libs.chapi.kotlin)

    implementation(libs.archguard.scanner.core)
    implementation(libs.archguard.analyser.estimate)

    // checkout
    implementation(libs.codedb.checkout)
    implementation(libs.codedb.action.toolkit)

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
    mainClass.set("cc.unitmesh.pick.MainKt")
}

tasks {
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "cc.unitmesh.pick.MainKt"))
        }
        // minimize()
        dependencies {
            exclude(dependency("org.junit.jupiter:.*:.*"))
            exclude(dependency("org.junit:.*:.*"))
            exclude(dependency("junit:.*:.*"))
        }
    }
}
