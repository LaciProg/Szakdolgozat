package hu.bme.aut.android.examapp.ui.viewmodel.multiplechoicequestion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.android.examapp.Type
import hu.bme.aut.android.examapp.api.dto.MultipleChoiceQuestionDto
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface MultipleChoiceQuestionEntryScreenUiState {
    data object Success : MultipleChoiceQuestionEntryScreenUiState
    data object Error : MultipleChoiceQuestionEntryScreenUiState{var errorMessage: String = ""}
    data object Loading : MultipleChoiceQuestionEntryScreenUiState
}

class MultipleChoiceQuestionEntryViewModel: ViewModel(){

    var multipleChoiceQuestionUiState by mutableStateOf(MultipleChoiceQuestionUiState())
        private set

    var multipleChoiceQuestionScreenUiState: MultipleChoiceQuestionEntryScreenUiState by mutableStateOf(MultipleChoiceQuestionEntryScreenUiState.Success)

    fun updateUiState(multipleChoiceQuestionDetails: MultipleChoiceQuestionDetails) {
        multipleChoiceQuestionUiState =
            MultipleChoiceQuestionUiState(
                multipleChoiceQuestionDetails = multipleChoiceQuestionDetails,
                isEntryValid = validateInput(multipleChoiceQuestionDetails,
                )
            )
    }

    suspend fun saveMultipleChoiceQuestion() : Boolean {
        return if (validateInput() && validateUniqueMultipleChoiceQuestion()) {
            try{
                viewModelScope.launch {
                    ApiService.postMultipleChoice(multipleChoiceQuestionUiState.multipleChoiceQuestionDetails.toMultipleChoiceQuestion())
                }
                true
            } catch (e: IOException){
                MultipleChoiceQuestionEntryScreenUiState.Error.errorMessage = "Network error"
                multipleChoiceQuestionScreenUiState = MultipleChoiceQuestionEntryScreenUiState.Error
                false
            } /*catch (e: HttpException){
                when(e.code()){
                    400 -> MultipleChoiceQuestionEntryScreenUiState.Error.errorMessage = "Bad request"
                    401 -> MultipleChoiceQuestionEntryScreenUiState.Error.errorMessage = "Unauthorized try logging in again or open the home screen"
                    404 -> MultipleChoiceQuestionEntryScreenUiState.Error.errorMessage = "Content not found"
                    500 -> MultipleChoiceQuestionEntryScreenUiState.Error.errorMessage = "Server error"
                    else -> MultipleChoiceQuestionEntryScreenUiState.Error
                }
                multipleChoiceQuestionScreenUiState = MultipleChoiceQuestionEntryScreenUiState.Error
                false
            }*/
        } else {
            multipleChoiceQuestionUiState = multipleChoiceQuestionUiState.copy(isEntryValid = false)
            false
        }
    }


    private fun validateInput(uiState: MultipleChoiceQuestionDetails = multipleChoiceQuestionUiState.multipleChoiceQuestionDetails): Boolean {
        return with(uiState) {
            question.isNotBlank() && isAnswerChosen && point!= "" && topic != "" && !question.contains("/")
        }
    }

    private suspend fun validateUniqueMultipleChoiceQuestion(uiState: MultipleChoiceQuestionDetails = multipleChoiceQuestionUiState.multipleChoiceQuestionDetails): Boolean {
        return try{
            !ApiService.getAllMultipleChoiceNames().map{it.name}.contains(uiState.question)
        } catch (e: IOException) {
            MultipleChoiceQuestionEntryScreenUiState.Error.errorMessage = "Network error"
            multipleChoiceQuestionScreenUiState = MultipleChoiceQuestionEntryScreenUiState.Error
            false
        } /*catch (e: HttpException){
            when(e.code()){
                400 -> MultipleChoiceQuestionEntryScreenUiState.Error.errorMessage = "Bad request"
                401 -> MultipleChoiceQuestionEntryScreenUiState.Error.errorMessage = "Unauthorized try logging in again or open the home screen"
                404 -> MultipleChoiceQuestionEntryScreenUiState.Error.errorMessage = "Content not found"
                500 -> MultipleChoiceQuestionEntryScreenUiState.Error.errorMessage = "Server error"
                else -> MultipleChoiceQuestionEntryScreenUiState.Error
            }
            multipleChoiceQuestionScreenUiState = MultipleChoiceQuestionEntryScreenUiState.Error
            false
        }*/
    }

}

data class MultipleChoiceQuestionUiState(
    val multipleChoiceQuestionDetails: MultipleChoiceQuestionDetails = MultipleChoiceQuestionDetails(),
    val isEntryValid: Boolean = false
)

data class MultipleChoiceQuestionDetails(
    val id: String = "",
    val question: String = "",
    val answers: MutableList<String> = mutableListOf(""),
    val correctAnswersList: MutableList<String> = mutableListOf(""),
    val point: String = "",
    val topic: String = "",
    val isAnswerChosen: Boolean = false,
    val pointName: String = "",
    val topicName: String = ""
)

fun MultipleChoiceQuestionDetails.toMultipleChoiceQuestion() = MultipleChoiceQuestionDto(
    uuid = id,
    question = question,
    answers = answers,
    correctAnswersList = correctAnswersList,
    point = point,
    topic = topic,
    type = Type.multipleChoiceQuestion.name,
)



fun MultipleChoiceQuestionDto.toMultipleChoiceQuestionUiState(isEntryValid: Boolean = false, pointName: String, topicName: String, isAnswerChosen: Boolean = false): MultipleChoiceQuestionUiState = MultipleChoiceQuestionUiState(
    multipleChoiceQuestionDetails = this.toMultipleChoiceQuestionDetails(pointName = pointName, topicName =  topicName, isAnswerChosen),
    isEntryValid = isEntryValid
)

fun MultipleChoiceQuestionDto.toMultipleChoiceQuestionDetails(pointName: String, topicName: String, isAnswerChosen: Boolean = false): MultipleChoiceQuestionDetails = MultipleChoiceQuestionDetails(
    id = uuid,
    question = question,
    answers = answers.toMutableList(),
    correctAnswersList = correctAnswersList.toMutableList(),
    point = point,
    topic = topic,
    pointName = pointName,
    topicName = topicName,
    isAnswerChosen = isAnswerChosen
)

