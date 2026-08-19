package io.mcdk

plugins {
    id("io.mcdk.kotlin-jvm")
}

tasks.processResources {
    val resourceTargets = listOf("META-INF/plugin.jsonc")
    val replaceProperties = mapOf(
        "version" to project.version
    )
    filesMatching(resourceTargets) {
        expand(replaceProperties)
    }
}