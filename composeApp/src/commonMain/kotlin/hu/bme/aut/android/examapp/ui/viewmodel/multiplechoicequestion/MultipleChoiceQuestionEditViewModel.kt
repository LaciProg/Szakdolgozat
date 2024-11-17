package hu.bme.aut.android.examapp.ui.viewmodel.multiplechoicequestion

import ApiException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.android.examapp.service.api.dto.*
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.launch

sealed interface MultipleChoiceQuestionEditScreenUiState {
    data class Success(val question: MultipleChoiceQuestionDto) : MultipleChoiceQuestionEditScreenUiState
    data object Error : MultipleChoiceQuestionEditScreenUiState{var errorMessage: String = ""}
    data object Loading : MultipleChoiceQuestionEditScreenUiState
}

class MultipleChoiceQuestionEditViewModel(
    var multipleChoiceQuestionId: String,
) : ViewModel() {

    private lateinit var originalQuestion: String

    var multipleChoiceQuestionUiState by mutableStateOf(MultipleChoiceQuestionUiState())
        private set

    var multipleChoiceEditScreenUiState: MultipleChoiceQuestionEditScreenUiState by mutableStateOf(
        MultipleChoiceQuestionEditScreenUiState.Loading)

    init {
        getMultipleChoiceQuestion(multipleChoiceQuestionId)
    }

    fun setId(id: String){
        multipleChoiceQuestionId = id
        getMultipleChoiceQuestion(id)
    }

    fun getMultipleChoiceQuestion(topicId: String){
        multipleChoiceEditScreenUiState = MultipleChoiceQuestionEditScreenUiState.Loading
        viewModelScope.launch {
            multipleChoiceEditScreenUiState = try{
                val result = ApiService.getMultipleChoice(topicId)
                multipleChoiceQuestionUiState = result.toMultipleChoiceQuestionUiState(isEntryValid = true,
                    topicName =
                    if (result.topic == "null") ""
                    else ApiService.getTopic(result.topic).topic,
                    pointName =
                    if (result.point == "null") ""
                    else ApiService.getPoint(result.point).type,
                    isAnswerChosen = true
                )
                originalQuestion = multipleChoiceQuestionUiState.multipleChoiceQuestionDetails.question
                MultipleChoiceQuestionEditScreenUiState.Success(result)
            } catch (e: IOException) {
                MultipleChoiceQuestionEditScreenUiState.Error
            } catch (e: ApiException) {
                MultipleChoiceQuestionEditScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                MultipleChoiceQuestionEditScreenUiState.Error
            } catch (e: Exception){
                MultipleChoiceQuestionEditScreenUiState.Error.errorMessage = "Network error"
                MultipleChoiceQuestionEditScreenUiState.Error
            }
        }
    }

    suspend fun updateMultipleChoiceQuestion() : Boolean {
        return if (validateInput(multipleChoiceQuestionUiState.multipleChoiceQuestionDetails) && validateUniqueMultipleChoiceQuestion(multipleChoiceQuestionUiState.multipleChoiceQuestionDetails)) {
            try {
                viewModelScope.launch {
                    multipleChoiceQuestionUiState.multipleChoiceQuestionDetails.correctAnswersList.removeIf(
                        String::isBlank
                    )
                    ApiService.updateMultipleChoice(multipleChoiceQuestionUiState.multipleChoiceQuestionDetails.toMultipleChoiceQuestion())
                }
                return true
            } catch (e: IOException) {
                MultipleChoiceQuestionEditScreenUiState.Error.errorMessage = "Network error"
                multipleChoiceEditScreenUiState = MultipleChoiceQuestionEditScreenUiState.Error
                return false
            } catch (e: ApiException) {
                MultipleChoiceQuestionEditScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                MultipleChoiceQuestionEditScreenUiState.Error
                return false
            } catch (e: Exception){
                MultipleChoiceQuestionEditScreenUiState.Error.errorMessage = "Network error"
                MultipleChoiceQuestionEditScreenUiState.Error
                return false
            }
        }
        else {
            multipleChoiceQuestionUiState = multipleChoiceQuestionUiState.copy(isEntryValid = false)
            false
        }
    }

    fun updateUiState(multipleChoiceQuestionDetails: MultipleChoiceQuestionDetails) {
        multipleChoiceQuestionUiState =
            MultipleChoiceQuestionUiState(multipleChoiceQuestionDetails = multipleChoiceQuestionDetails, isEntryValid = validateInput(multipleChoiceQuestionDetails))
    }

    private fun validateInput(uiState: MultipleChoiceQuestionDetails = multipleChoiceQuestionUiState.multipleChoiceQuestionDetails): Boolean {
        return with(uiState) {
            question.isNotBlank() && isAnswerChosen && point!= "" && topic != "" && !question.contains("/")
        }
    }

    private suspend fun validateUniqueMultipleChoiceQuestion(uiState: MultipleChoiceQuestionDetails = multipleChoiceQuestionUiState.multipleChoiceQuestionDetails): Boolean {
        return !ApiService.getAllMultipleChoiceNames().map{it.name}.contains(uiState.question) || originalQuestion == uiState.question
    }
}