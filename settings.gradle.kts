plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "UnitEval"

// the abstract interface
include("unit-core")

// the code quality analysis codes
include("code-quality")

// picker code by rules
include("unit-picker")
// eval output by rules
include("unit-eval")
