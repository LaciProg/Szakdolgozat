package hu.bme.aut.android.examapp.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class StatisticsDto(
    val earnedPoints: Double,
    val percentage: Double
)