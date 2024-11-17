package hu.bme.aut.android.examapp.ui.viewmodel.multiplechoicequestion

import ApiException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.android.examapp.service.api.dto.*
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface MultipleChoiceQuestionDetailsScreenUiState {
    data class Success(val question: MultipleChoiceQuestionDto) : MultipleChoiceQuestionDetailsScreenUiState
    data object Error : MultipleChoiceQuestionDetailsScreenUiState{var errorMessage: String = ""}
    data object Loading : MultipleChoiceQuestionDetailsScreenUiState
}

class MultipleChoiceQuestionDetailsViewModel(
    var multipleChoiceQuestionId: String,
) : ViewModel() {

    var multipleChoiceDetailsScreenUiState: MultipleChoiceQuestionDetailsScreenUiState by mutableStateOf(
        MultipleChoiceQuestionDetailsScreenUiState.Loading)
    var uiState by mutableStateOf(MultipleChoiceQuestionDetailsUiState())
    init {
        getQuestion(multipleChoiceQuestionId)
    }

    fun setId(id: String){
        multipleChoiceQuestionId = id
        getQuestion(id)
    }

    fun getQuestion(topicId: String){
        multipleChoiceDetailsScreenUiState = MultipleChoiceQuestionDetailsScreenUiState.Loading
        viewModelScope.launch {
            multipleChoiceDetailsScreenUiState = try{
                val result = ApiService.getMultipleChoice(topicId)
                uiState = MultipleChoiceQuestionDetailsUiState(result.toMultipleChoiceQuestionDetails(
                    topicName =
                    if (result.topic == "null") ""
                    else ApiService.getTopic(result.topic).topic,
                    pointName =
                    if (result.point == "null") ""
                    else ApiService.getPoint(result.point).type,
                ))
                MultipleChoiceQuestionDetailsScreenUiState.Success(result)
            } catch (e: IOException) {
                MultipleChoiceQuestionDetailsScreenUiState.Error
            } catch (e: ApiException) {
                MultipleChoiceQuestionDetailsScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                MultipleChoiceQuestionDetailsScreenUiState.Error
            } catch (e: Exception){
                MultipleChoiceQuestionDetailsScreenUiState.Error.errorMessage = "Network error"
                MultipleChoiceQuestionDetailsScreenUiState.Error
            }
        }
    }

    suspend fun deleteMultipleChoiceQuestion() {
        try{
            ApiService.deleteMultipleChoice(multipleChoiceQuestionId)
        } catch (e: IOException) {
            MultipleChoiceQuestionDetailsScreenUiState.Error.errorMessage = "Network error"
            multipleChoiceDetailsScreenUiState = MultipleChoiceQuestionDetailsScreenUiState.Error
        } catch (e: ApiException) {
            MultipleChoiceQuestionDetailsScreenUiState.Error.errorMessage = e.message?: "Unkown error"
            multipleChoiceDetailsScreenUiState = MultipleChoiceQuestionDetailsScreenUiState.Error
        } catch (e: Exception){
            MultipleChoiceQuestionDetailsScreenUiState.Error.errorMessage = "Network error"
            multipleChoiceDetailsScreenUiState = MultipleChoiceQuestionDetailsScreenUiState.Error
        }
    }

}

/**
 * UI state for TopicDetailsScreen
 */
data class MultipleChoiceQuestionDetailsUiState(
    val multipleChoiceQuestionDetails: MultipleChoiceQuestionDetails = MultipleChoiceQuestionDetails()
)