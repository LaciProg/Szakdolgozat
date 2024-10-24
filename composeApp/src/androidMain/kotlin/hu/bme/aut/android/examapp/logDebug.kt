package hu.bme.aut.android.examapp

import android.util.Log

actual fun logDebug(tag: String, message: String) {
    Log.d(tag, message)
}