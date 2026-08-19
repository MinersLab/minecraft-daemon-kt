package io.mcdk

import io.mcdk.util.platform.Version
import java.util.Properties

public object McdkMetadata {

    private val properties: Properties = Properties().apply {
        val inputStream = McdkMetadata::class.java.getResourceAsStream("/META-INF/mcdk.properties")
        if (inputStream != null) {
            load(inputStream)
            inputStream.close()
        } else {
            throw IllegalStateException("Could not find mcdk.properties file in resources")
        }
    }

    @JvmField
    public val version: Version = Version(properties.getProperty("version")!!)

    @JvmField
    public val buildDate: String = properties.getProperty("build.date")!!

    @JvmField
    public val gitHash: String = properties.getProperty("git.hash")!!

    @JvmField
    public val gitBranch: String = properties.getProperty("git.branch")!!

}