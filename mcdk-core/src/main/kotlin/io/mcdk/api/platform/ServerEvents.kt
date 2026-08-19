package io.mcdk.api.platform

import io.mcdk.api.command.session.CommandSender
import io.mcdk.api.event.AbstractEventType
import io.mcdk.api.event.IEvent

public object ServerEvents {

    public data class ReceiveCommandEvent(
        public val command: String,
        public val sender: CommandSender,
        public val platform: Platform<*>
    ) : AbstractEventType<ReceiveCommandEvent, Unit>(EVENT) {
        public companion object {
            @JvmField public val EVENT: IEvent<ReceiveCommandEvent, Unit> = IEvent.create()
        }
    }

}