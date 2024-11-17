package hu.bme.aut.android.examapp.service.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class StatisticsDto(
    val earnedPoints: Double,
    val percentage: Double
)