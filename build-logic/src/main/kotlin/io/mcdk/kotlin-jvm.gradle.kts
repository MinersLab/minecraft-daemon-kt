package io.mcdk

version = "1.0.0"
group = "io.mcdk"

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

kotlin {
    explicitApi()
    jvmToolchain(25)
}

tasks.compileJava {
    options.encoding = "UTF-8"
}
