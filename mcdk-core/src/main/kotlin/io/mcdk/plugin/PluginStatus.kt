package io.mcdk.plugin

public enum class PluginStatus(
    public val isEnabled: Boolean = false
) {

    IDLE, ERROR, LOADING, LOADED(true), CLOSED;

}