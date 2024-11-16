package hu.bme.aut.android.examapp.api.dto


import hu.bme.aut.android.examapp.Type
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MultipleChoiceQuestionDto(
    val uuid: String = "",
    val question: String,
    val answers: List<String>,
    val correctAnswersList: List<String>,
    val point: String,
    val topic: String,
    @SerialName("type_id")
    val type: String
): Question {
    override val typeOrdinal: Int
        get() = Type.multipleChoiceQuestion.ordinal
}