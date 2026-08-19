package io.mcdk.api.event

public interface EventType<R> {
    public fun fire(): R
}

public abstract class AbstractEventType<T, R>(public val event: IEvent<T, R>) : EventType<R> {
    @Suppress("UNCHECKED_CAST")
    override fun fire(): R = event.invoke(this as T)
}
