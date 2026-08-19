package io.mcdk.util.logging.slf4j

import io.mcdk.util.logging.ILogger
import io.mcdk.util.logging.LogLevel
import org.slf4j.Logger
import org.slf4j.Marker
import org.slf4j.helpers.MessageFormatter
import io.mcdk.util.logging.LoggerFactory as LoggerFactoryImpl

internal class Slf4jLogger(private val name: String) : Logger {

    private val delegate: ILogger = LoggerFactoryImpl.getLogger(name)

    private fun format(format: String, args: Array<out Any?>): Pair<String, Throwable?> {
        val ft = MessageFormatter.arrayFormat(format, args)
        return Pair(ft.message, ft.throwable)
    }

    override fun getName(): String = name

    // TRACE
    override fun isTraceEnabled(): Boolean = delegate[LogLevel.TRACE]
    override fun trace(msg: String?) {
        if (msg != null) delegate.trace(msg)
    }

    override fun trace(format: String?, arg: Any?) {
        if (format != null && isTraceEnabled()) {
            val (m, t) = format(format, arrayOf(arg)); delegate.trace(m, t)
        }
    }

    override fun trace(format: String?, arg1: Any?, arg2: Any?) {
        if (format != null && isTraceEnabled()) {
            val (m, t) = format(format, arrayOf(arg1, arg2)); delegate.trace(m, t)
        }
    }

    override fun trace(format: String?, vararg arguments: Any?) {
        if (format != null && isTraceEnabled()) {
            val (m, t) = format(format, arguments); delegate.trace(m, t)
        }
    }

    override fun trace(msg: String?, t: Throwable?) {
        if (msg != null) delegate.trace(msg, t) else if (t != null) delegate.trace(t.toString(), t)
    }

    override fun isTraceEnabled(marker: Marker?): Boolean = isTraceEnabled()
    override fun trace(marker: Marker?, msg: String?) {
        trace(msg)
    }

    override fun trace(marker: Marker?, format: String?, arg: Any?) {
        trace(format, arg)
    }

    override fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        trace(format, arg1, arg2)
    }

    override fun trace(marker: Marker?, format: String?, vararg arguments: Any?) {
        trace(format, *arguments)
    }

    override fun trace(marker: Marker?, msg: String?, t: Throwable?) {
        trace(msg, t)
    }

    // DEBUG
    override fun isDebugEnabled(): Boolean = delegate[LogLevel.DEBUG]
    override fun debug(msg: String?) {
        if (msg != null) delegate.debug(msg)
    }

    override fun debug(format: String?, arg: Any?) {
        if (format != null && isDebugEnabled()) {
            val (m, t) = format(format, arrayOf(arg)); delegate.debug(m, t)
        }
    }

    override fun debug(format: String?, arg1: Any?, arg2: Any?) {
        if (format != null && isDebugEnabled()) {
            val (m, t) = format(format, arrayOf(arg1, arg2)); delegate.debug(m, t)
        }
    }

    override fun debug(format: String?, vararg arguments: Any?) {
        if (format != null && isDebugEnabled()) {
            val (m, t) = format(format, arguments); delegate.debug(m, t)
        }
    }

    override fun debug(msg: String?, t: Throwable?) {
        if (msg != null) delegate.debug(msg, t) else if (t != null) delegate.debug(t.toString(), t)
    }

    override fun isDebugEnabled(marker: Marker?): Boolean = isDebugEnabled()
    override fun debug(marker: Marker?, msg: String?) {
        debug(msg)
    }

    override fun debug(marker: Marker?, format: String?, arg: Any?) {
        debug(format, arg)
    }

    override fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        debug(format, arg1, arg2)
    }

    override fun debug(marker: Marker?, format: String?, vararg arguments: Any?) {
        debug(format, *arguments)
    }

    override fun debug(marker: Marker?, msg: String?, t: Throwable?) {
        debug(msg, t)
    }

    // INFO
    override fun isInfoEnabled(): Boolean = delegate[LogLevel.INFO]
    override fun info(msg: String?) {
        if (msg != null) delegate.info(msg)
    }

    override fun info(format: String?, arg: Any?) {
        if (format != null && isInfoEnabled()) {
            val (m, t) = format(format, arrayOf(arg)); delegate.info(m, t)
        }
    }

    override fun info(format: String?, arg1: Any?, arg2: Any?) {
        if (format != null && isInfoEnabled()) {
            val (m, t) = format(format, arrayOf(arg1, arg2)); delegate.info(m, t)
        }
    }

    override fun info(format: String?, vararg arguments: Any?) {
        if (format != null && isInfoEnabled()) {
            val (m, t) = format(format, arguments); delegate.info(m, t)
        }
    }

    override fun info(msg: String?, t: Throwable?) {
        if (msg != null) delegate.info(msg, t) else if (t != null) delegate.info(t.toString(), t)
    }

    override fun isInfoEnabled(marker: Marker?): Boolean = isInfoEnabled()
    override fun info(marker: Marker?, msg: String?) {
        info(msg)
    }

    override fun info(marker: Marker?, format: String?, arg: Any?) {
        info(format, arg)
    }

    override fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        info(format, arg1, arg2)
    }

    override fun info(marker: Marker?, format: String?, vararg arguments: Any?) {
        info(format, *arguments)
    }

    override fun info(marker: Marker?, msg: String?, t: Throwable?) {
        info(msg, t)
    }

    // WARN
    override fun isWarnEnabled(): Boolean = delegate[LogLevel.WARN]
    override fun warn(msg: String?) {
        if (msg != null) delegate.warn(msg)
    }

    override fun warn(format: String?, arg: Any?) {
        if (format != null && isWarnEnabled()) {
            val (m, t) = format(format, arrayOf(arg)); delegate.warn(m, t)
        }
    }

    override fun warn(format: String?, arg1: Any?, arg2: Any?) {
        if (format != null && isWarnEnabled()) {
            val (m, t) = format(format, arrayOf(arg1, arg2)); delegate.warn(m, t)
        }
    }

    override fun warn(format: String?, vararg arguments: Any?) {
        if (format != null && isWarnEnabled()) {
            val (m, t) = format(format, arguments); delegate.warn(m, t)
        }
    }

    override fun warn(msg: String?, t: Throwable?) {
        if (msg != null) delegate.warn(msg, t) else if (t != null) delegate.warn(t.toString(), t)
    }

    override fun isWarnEnabled(marker: Marker?): Boolean = isWarnEnabled()
    override fun warn(marker: Marker?, msg: String?) {
        warn(msg)
    }

    override fun warn(marker: Marker?, format: String?, arg: Any?) {
        warn(format, arg)
    }

    override fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        warn(format, arg1, arg2)
    }

    override fun warn(marker: Marker?, format: String?, vararg arguments: Any?) {
        warn(format, *arguments)
    }

    override fun warn(marker: Marker?, msg: String?, t: Throwable?) {
        warn(msg, t)
    }

    // ERROR
    override fun isErrorEnabled(): Boolean = delegate[LogLevel.ERROR]
    override fun error(msg: String?) {
        if (msg != null) delegate.error(msg)
    }

    override fun error(format: String?, arg: Any?) {
        if (format != null && isErrorEnabled()) {
            val (m, t) = format(format, arrayOf(arg)); delegate.error(m, t)
        }
    }

    override fun error(format: String?, arg1: Any?, arg2: Any?) {
        if (format != null && isErrorEnabled()) {
            val (m, t) = format(format, arrayOf(arg1, arg2)); delegate.error(m, t)
        }
    }

    override fun error(format: String?, vararg arguments: Any?) {
        if (format != null && isErrorEnabled()) {
            val (m, t) = format(format, arguments); delegate.error(m, t)
        }
    }

    override fun error(msg: String?, t: Throwable?) {
        if (msg != null) delegate.error(msg, t) else if (t != null) delegate.error(t.toString(), t)
    }

    override fun isErrorEnabled(marker: Marker?): Boolean = isErrorEnabled()
    override fun error(marker: Marker?, msg: String?) {
        error(msg)
    }

    override fun error(marker: Marker?, format: String?, arg: Any?) {
        error(format, arg)
    }

    override fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {
        error(format, arg1, arg2)
    }

    override fun error(marker: Marker?, format: String?, vararg arguments: Any?) {
        error(format, *arguments)
    }

    override fun error(marker: Marker?, msg: String?, t: Throwable?) {
        error(msg, t)
    }

}
