package hu.bme.aut.android.examapp

actual fun logDebug(tag: String, message: String) {
    println("DEBUG: [$tag] $message")
}