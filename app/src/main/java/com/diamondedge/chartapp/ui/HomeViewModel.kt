package com.diamondedge.chartapp.ui

import androidx.lifecycle.ViewModel
import org.lighthousegames.logging.logging

class HomeViewModel : ViewModel() {
    val log = logging()

    init {
        log.d { "Initializing" }
    }

}
