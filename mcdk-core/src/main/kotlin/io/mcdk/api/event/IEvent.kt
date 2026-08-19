package io.mcdk.api.event

import io.mcdk.core.Identifier

public interface IEvent<T, R> {

    public companion object {
        @JvmField
        public val DEFAULT_PHASE: Identifier = Identifier("default")

        public fun <T, R> create(invoker: ((listeners: List<(T) -> R>) -> (T) -> R)? = null): IEvent<T, R> {
            return EventImpl(invoker)
        }
    }

    public fun register(phase: Identifier = DEFAULT_PHASE, listener: (T) -> R): (T) -> R
    public fun unregister(listener: (T) -> R, phase: Identifier = DEFAULT_PHASE)
    public val invoke: (T) -> R
    public fun order(before: Identifier, after: Identifier)

}
