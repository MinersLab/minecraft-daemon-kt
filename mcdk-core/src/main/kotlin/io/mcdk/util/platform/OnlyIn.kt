package io.mcdk.util.platform

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
public annotation class OnlyIn(
    public val mcVersionRange: String,
    public vararg val platforms: String
)
