package hu.bme.aut.android.examapp.service.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class NameDto(
    val name: String,
    val uuid: String
)