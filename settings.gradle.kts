plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "mcdk"

includeBuild("build-logic")
include(":mcdk-core")
include(":mcdk-launcher")
include(":mcdk-plugin-platform-vanilla")