package com.diamondedge.charts

import org.lighthousegames.logging.KmLogging
import org.lighthousegames.logging.KmModuleLog
import org.lighthousegames.logging.logging

object ChartsLogging {
    var enabled = false
}

fun moduleLogging(tag: String? = null): KmModuleLog {
    val t = tag ?: KmLogging.createTag("ChartsLoggingKt").first
    return KmModuleLog(logging(t), ChartsLogging::enabled)
}
