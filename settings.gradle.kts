@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "UnitGen"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

include(
    "unit-core",
    "unit-picker",
    "unit-gen",
    "unit-distillation",

    "code-quality",

    "examples:project-example",
)
