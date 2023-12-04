plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "UnitEval"

// the abstract interface
include("unit-core")
// picker code by rules
include("unit-picker")
// eval output by rules
include("unit-eval")
