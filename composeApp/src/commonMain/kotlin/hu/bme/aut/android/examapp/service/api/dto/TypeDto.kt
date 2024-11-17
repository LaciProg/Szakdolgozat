package hu.bme.aut.android.examapp.service.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class TypeDto(
    val uuid: String = "",
    val type: String
)