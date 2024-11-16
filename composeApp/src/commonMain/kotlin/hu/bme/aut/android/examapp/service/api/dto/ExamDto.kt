package hu.bme.aut.android.examapp.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExamDto(
    val uuid: String = "",
    val name: String,

    //@Serializable(with = TrueFalseQuestionSerializer::class)
    //@Serializable(with = MultipleChoiceSerializer::class)
    val questionList: String, //List<Question?>
    val topicId: String
)