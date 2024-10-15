package hu.bme.aut.android.examapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform