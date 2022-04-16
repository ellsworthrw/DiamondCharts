package com.diamondedge.chart

import org.lighthousegames.logging.KmLog
import org.lighthousegames.logging.KmLogging
import org.lighthousegames.logging.logging

object ChartsLogging {
    var enabled = true
}

fun moduleLogging(tag: String? = null): KmModuleLog {
    val (tagCalculated, className) = KmLogging.createTag("ChartsLoggingKt")
    // TODO: use moduleLogging(tag, tagCalculated, className)
    val t = tag ?: tagCalculated
    return KmModuleLog(logging(t), ChartsLogging::enabled)
}

class KmModuleLog(val log: KmLog, val isModuleLogging: () -> Boolean) {

    inline fun v(msg: () -> Any?) {
        if (isModuleLogging())
            log.verbose(msg)
    }

    inline fun v(tag: String, msg: () -> Any?) {
        if (isModuleLogging())
            log.verbose(tag, msg)
    }

    inline fun d(msg: () -> Any?) {
        if (isModuleLogging())
            log.debug(msg)
    }

    inline fun d(tag: String, msg: () -> Any?) {
        if (isModuleLogging())
            log.debug(tag, msg)
    }

    inline fun i(msg: () -> Any?) {
        if (isModuleLogging())
            log.info(msg)
    }

    inline fun i(tag: String, msg: () -> Any?) {
        if (isModuleLogging())
            log.info(tag, msg)
    }

    inline fun w(msg: () -> Any?) {
        if (isModuleLogging())
            log.warn(msg)
    }

    inline fun w(err: Throwable?, tag: String? = null, msg: () -> Any?) {
        if (isModuleLogging())
            log.warn(err, tag, msg)
    }

    inline fun e(msg: () -> Any?) {
        if (isModuleLogging())
            log.error(msg)
    }

    inline fun e(err: Throwable?, tag: String? = null, msg: () -> Any?) {
        if (isModuleLogging())
            log.error(err, tag, msg)
    }
}
