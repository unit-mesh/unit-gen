@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "UnitMeshEval"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

include(
    "unit-core",
    "unit-picker",
    "unit-eval",
    "unit-cli",

    "code-quality",

//    "examples:project-example",
)
