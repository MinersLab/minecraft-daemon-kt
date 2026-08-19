package io.mcdk.api.event

import io.mcdk.core.Identifier

internal class EventImpl<T, R>(private val invoker: ((listeners: List<(T) -> R>) -> (T) -> R)? = null) : IEvent<T, R> {

    private val phases: MutableMap<Identifier, MutableList<(T) -> R>> = mutableMapOf()
    private val phaseOrder: MutableList<Identifier> = mutableListOf(IEvent.DEFAULT_PHASE)

    @Suppress("UNCHECKED_CAST")
    override val invoke: (T) -> R
        get() {
            val listeners = phaseOrder.asSequence()
                .mapNotNull { phases[it] }
                .flatten()
                .toList()
            return if (invoker == null) {
                {
                    var result: R? = null
                    for (listener in listeners) {
                        result = listener(it)
                    }
                    result as R
                }
            } else {
                invoker.invoke(listeners)
            }
        }

    override fun order(before: Identifier, after: Identifier) {
        if (before == after) return

        if (before !in phaseOrder) {
            phaseOrder.add(before)
        }
        if (after !in phaseOrder) {
            phaseOrder.add(after)
        }

        val beforeIndex = phaseOrder.indexOf(before)
        val afterIndex = phaseOrder.indexOf(after)
        if (beforeIndex < afterIndex) return

        phaseOrder.removeAt(beforeIndex)
        phaseOrder.add(phaseOrder.indexOf(after), before)
    }

    override fun register(phase: Identifier, listener: (T) -> R) = listener.also {
        phases.getOrPut(phase, ::mutableListOf).add(listener)
    }

    override fun unregister(listener: (T) -> R, phase: Identifier) {
        phases[phase]?.remove(listener)
    }

}