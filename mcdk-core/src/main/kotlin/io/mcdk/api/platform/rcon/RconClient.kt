package io.mcdk.api.platform.rcon

import org.glavo.rcon.Rcon

public class RconClient(
    public val host: String,
    public val port: Int,
    public val password: String
) : AutoCloseable {

    private val rcon: Rcon = Rcon()

    public val isAlive: Boolean
        get() = rcon.socket.isConnected && !rcon.socket.isClosed

    public fun connect() {
        rcon.connect(host, port, password.toByteArray())
    }

    public fun disconnect() {
        rcon.disconnect()
    }

    public fun retrieve(command: String): String {
        return rcon.command(command)
    }

    override fun close() {
        rcon.close()
    }

}