package hu.bme.aut.android.examapp.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class NameDto(
    val name: String,
    val uuid: String
)