package com.example.doordashproject

import android.util.Log
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/* Rudimentary exception handling for this project */

object ExceptionHandler {
    var logError = { tag: String, message: String? -> Log.d(tag, message ?: "no message") }
    var toast = { message: String? -> logError("TOAST", message) }
    var dialog = { message: String? -> logError("DIALOG", message) }
    var snackbar = { message: String? -> logError("SNACKBAR", message) }
    var debugInfo = { message: String? -> logError("INFO", message) }
}