plugins {
    id("io.mcdk.plugin")
    application
}

dependencies {
    api(project(":mcdk-core"))
}

application {
    mainClass = "io.mcdk.launcher.LauncherKt"
}
