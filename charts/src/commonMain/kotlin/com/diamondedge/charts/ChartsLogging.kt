package com.diamondedge.charts

import com.diamondedge.logging.KmLogging
import com.diamondedge.logging.KmModuleLog
import com.diamondedge.logging.logging

object ChartsLogging {
    var enabled = false
}

fun moduleLogging(tag: String? = null): KmModuleLog {
    val t = tag ?: KmLogging.createTag("ChartsLoggingKt").first
    return KmModuleLog(logging(t), ChartsLogging::enabled)
}
