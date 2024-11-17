package hu.bme.aut.android.examapp.service.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class PointDto(
    val uuid: String = "",
    val point: Double,
    val type: String,
    val goodAnswer: Double = 2.0,
    val badAnswer: Double = -2.0,
)