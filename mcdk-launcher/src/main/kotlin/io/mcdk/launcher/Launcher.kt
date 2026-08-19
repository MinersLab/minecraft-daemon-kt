package io.mcdk.launcher

import io.mcdk.Mcdk
import io.mcdk.mcdk
import io.mcdk.util.ref
import java.io.File

public fun main() {
    ref(::mcdk).set(
        Mcdk(File(System.getProperty("user.dir"), ".mcdk").absoluteFile)
    )
    mcdk.start()
}