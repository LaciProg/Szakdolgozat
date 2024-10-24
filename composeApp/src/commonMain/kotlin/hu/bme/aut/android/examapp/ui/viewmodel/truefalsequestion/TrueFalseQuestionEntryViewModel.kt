package hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.android.examapp.Type
import hu.bme.aut.android.examapp.api.dto.TrueFalseQuestionDto
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.launch

sealed interface TrueFalseQuestionEntryScreenUiState {
    data object Success : TrueFalseQuestionEntryScreenUiState
    data object Error : TrueFalseQuestionEntryScreenUiState{var errorMessage: String = ""}
    data object Loading : TrueFalseQuestionEntryScreenUiState
}

class TrueFalseQuestionEntryViewModel: ViewModel(){

    var trueFalseQuestionUiState by mutableStateOf(TrueFalseQuestionUiState())
        private set

    var trueFalseEntryScreenUiState: TrueFalseQuestionEntryScreenUiState by mutableStateOf(TrueFalseQuestionEntryScreenUiState.Success)

    fun updateUiState(trueFalseQuestionDetails: TrueFalseQuestionDetails) {
        trueFalseQuestionUiState =
            TrueFalseQuestionUiState(
                trueFalseQuestionDetails = trueFalseQuestionDetails,
                isEntryValid = validateInput(trueFalseQuestionDetails,
                )
            )
    }

    suspend fun saveTrueFalseQuestion() : Boolean {
        return if (validateInput() && validateUniqueTrueFalseQuestion()) {
            try {
                viewModelScope.launch {
                    ApiService.postTrueFalse(trueFalseQuestionUiState.trueFalseQuestionDetails.toTrueFalseQuestion())
                }
                true
            } catch (e: IOException){
                TrueFalseQuestionEditScreenUiState.Error.errorMessage = "Network error"
                trueFalseEntryScreenUiState = TrueFalseQuestionEntryScreenUiState.Error
                false
            } /*catch (e: HttpException){
                when(e.code()){
                    400 -> TrueFalseQuestionEntryScreenUiState.Error.errorMessage = "Bad request"
                    401 -> TrueFalseQuestionEntryScreenUiState.Error.errorMessage = "Unauthorized try logging in again or open the home screen"
                    404 -> TrueFalseQuestionEntryScreenUiState.Error.errorMessage = "Content not found"
                    500 -> TrueFalseQuestionEntryScreenUiState.Error.errorMessage = "Server error"
                    else -> TrueFalseQuestionEntryScreenUiState.Error
                }
                trueFalseEntryScreenUiState = TrueFalseQuestionEntryScreenUiState.Error
                false
            }*/
        }
        else{
            trueFalseQuestionUiState = trueFalseQuestionUiState.copy(isEntryValid = false)
            false
        }
    }

    private fun validateInput(uiState: TrueFalseQuestionDetails = trueFalseQuestionUiState.trueFalseQuestionDetails): Boolean {
        return with(uiState) {
            question.isNotBlank() && isAnswerChosen && point!= "" && topic != "" && !question.contains("/")
        }
    }

    private suspend fun validateUniqueTrueFalseQuestion(uiState: TrueFalseQuestionDetails = trueFalseQuestionUiState.trueFalseQuestionDetails): Boolean {
        return try{
            !ApiService.getAllTrueFalseNames().map{it.name}.contains(uiState.question)
        } catch (e: IOException) {
            TrueFalseQuestionEditScreenUiState.Error.errorMessage = "Network error"
            trueFalseEntryScreenUiState = TrueFalseQuestionEntryScreenUiState.Error
            false
        } /*catch (e: HttpException) {
            when(e.code()){
                400 -> TrueFalseQuestionEntryScreenUiState.Error.errorMessage = "Bad request"
                401 -> TrueFalseQuestionEntryScreenUiState.Error.errorMessage = "Unauthorized try logging in again or open the home screen"
                404 -> TrueFalseQuestionEntryScreenUiState.Error.errorMessage = "Content not found"
                500 -> TrueFalseQuestionEntryScreenUiState.Error.errorMessage = "Server error"
                else -> TrueFalseQuestionEntryScreenUiState.Error
            }
            trueFalseEntryScreenUiState = TrueFalseQuestionEntryScreenUiState.Error
            false
        }*/
    }

}

data class TrueFalseQuestionUiState(
    val trueFalseQuestionDetails: TrueFalseQuestionDetails = TrueFalseQuestionDetails(),
    val isEntryValid: Boolean = false
)

data class TrueFalseQuestionDetails(
    val id: String = "",
    val question: String = "",
    val correctAnswer: Boolean = false,
    val point: String = "",
    val topic: String = "",
    val isAnswerChosen: Boolean = false,
    val pointName: String = "",
    val topicName: String = ""
)

fun TrueFalseQuestionDetails.toTrueFalseQuestion(): TrueFalseQuestionDto = TrueFalseQuestionDto(
    uuid = id,
    question = question,
    correctAnswer = correctAnswer,
    point = point,
    topic = topic,
    type = Type.trueFalseQuestion.name,
)

fun TrueFalseQuestionDto.toTrueFalseQuestionUiState(isEntryValid: Boolean = false, pointName: String, topicName: String, isAnswerChosen: Boolean = false): TrueFalseQuestionUiState = TrueFalseQuestionUiState(
    trueFalseQuestionDetails = this.toTrueFalseQuestionDetails(pointName = pointName, topicName =  topicName, isAnswerChosen),
    isEntryValid = isEntryValid
)

fun TrueFalseQuestionDto.toTrueFalseQuestionDetails(pointName: String, topicName: String, isAnswerChosen: Boolean = false): TrueFalseQuestionDetails = TrueFalseQuestionDetails(
    id = uuid,
    question = question,
    correctAnswer = correctAnswer,
    point = point,
    topic = topic,
    pointName = pointName,
    topicName = topicName,
    isAnswerChosen = isAnswerChosen
)