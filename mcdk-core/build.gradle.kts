plugins {
    id("io.mcdk.kotlin-jvm")
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
