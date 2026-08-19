plugins {
    id("io.mcdk.kotlin-jvm")
    application
    alias(libs.plugins.shadow)
}

dependencies {
    api(project(":mcdk-core"))
}

application {
    mainClass = "io.mcdk.launcher.LauncherKt"
}

tasks.shadowJar {
    mergeServiceFiles()
}