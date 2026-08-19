import com.palantir.gradle.gitversion.VersionDetails
import groovy.lang.Closure
import java.util.Date

plugins {
    id("io.mcdk.kotlin-jvm")
    alias(libs.plugins.git.version)
}

dependencies {
    api(kotlin("reflect"))
    api(libs.kotlinx.serialization.json)
    api(libs.arrow.core)
    api(libs.ktor.server.core)
    api(libs.ktor.server.netty)
    api(libs.bundles.minecraft)
    api(libs.jline)
}

@Suppress("UNCHECKED_CAST")
val versionDetails = extra["versionDetails"] as Closure<VersionDetails>

tasks.processResources {
    filesMatching("META-INF/mcdk.properties") {
        expand(
            "version" to project.version.toString(),
            "date" to Date().toString(),
            "git" to mapOf(
                "hash" to versionDetails().gitHash,
                "branch" to versionDetails().branchName,
            )
        )
    }
}
