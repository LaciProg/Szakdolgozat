package hu.bme.aut.android.examapp.api.dto

import kotlinx.serialization.Serializable

@Serializable
sealed interface Question{
    val typeOrdinal: Int
}