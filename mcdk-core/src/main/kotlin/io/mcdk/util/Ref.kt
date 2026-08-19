package io.mcdk.util

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

public interface Ref<T> {
    public fun get(): T
    public fun set(value: T)
    public fun clear()
    public fun hasValue(): Boolean
}

public operator fun <T> Ref<T>.getValue(thisRef: Any?, property: KProperty<*>): T = get()
public operator fun <T> Ref<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T): Unit = set(value)

@Suppress("UNCHECKED_CAST")
public fun <T> ref(property: KProperty0<T>): Ref<T> {
    property.isAccessible = true
    return when (val delegate = property.getDelegate()) {
        is Ref<*> -> delegate as Ref<T>
        else -> throw IllegalStateException("Property ${property.name} is not delegated to a Ref")
    }
}

private class RefImpl<T>(private var value: Option<T>, private val once: Boolean, private val fallbackValue: () -> T) :
    Ref<T> {
    override fun get(): T {
        if (value is None) {
            value = Some(fallbackValue())
        }
        return (value as Some<T>).value
    }

    override fun set(value: T) {
        if (once && this.value is Some) {
            throw IllegalStateException("Ref can only be set once")
        }
        this.value = Some(value)
    }

    override fun clear() {
        this.value = None
    }

    override fun hasValue(): Boolean = value is Some
}


public fun <T> ref(
    initialValue: Option<T> = None,
    once: Boolean = false,
    fallbackValue: () -> T = { throw UninitializedPropertyAccessException("Ref is not initialized") }
): Ref<T> = RefImpl(initialValue, once, fallbackValue)

public fun <T> lateRef(once: Boolean = false, defaultThrowable: () -> Throwable): Ref<T> =
    ref(once = once) { throw defaultThrowable() }
