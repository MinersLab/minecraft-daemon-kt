package io.mcdk.util.logging.slf4j

import org.slf4j.ILoggerFactory
import org.slf4j.IMarkerFactory
import org.slf4j.helpers.BasicMDCAdapter as BasicMdcAdapter
import org.slf4j.helpers.BasicMarkerFactory
import org.slf4j.spi.MDCAdapter as MdcAdapter
import org.slf4j.spi.SLF4JServiceProvider as ISlf4jServiceProvider

public class Slf4jServiceProvider : ISlf4jServiceProvider {

    private val loggerFactory = ILoggerFactory { name -> Slf4jLogger(name ?: "") }
    override fun initialize() {}
    override fun getLoggerFactory(): ILoggerFactory = loggerFactory
    override fun getMarkerFactory(): IMarkerFactory = BasicMarkerFactory()
    override fun getMDCAdapter(): MdcAdapter = BasicMdcAdapter()
    override fun getRequestedApiVersion(): String = "2.0.99"

}
