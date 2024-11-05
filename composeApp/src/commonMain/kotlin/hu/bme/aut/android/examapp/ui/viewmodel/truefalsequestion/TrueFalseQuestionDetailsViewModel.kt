package hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.android.examapp.api.dto.TrueFalseQuestionDto
import hu.bme.aut.android.examapp.navigation.ExamDestination
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface TrueFalseQuestionDetailsScreenUiState {
    data class Success(val question: TrueFalseQuestionDto) : TrueFalseQuestionDetailsScreenUiState
    data object Error : TrueFalseQuestionDetailsScreenUiState{var errorMessage: String = ""}
    data object Loading : TrueFalseQuestionDetailsScreenUiState
}


class TrueFalseQuestionDetailsViewModel(
    var trueFalseQuestionId: String,
) : ViewModel() {

    var trueFalseDetailsScreenUiState: TrueFalseQuestionDetailsScreenUiState by mutableStateOf(TrueFalseQuestionDetailsScreenUiState.Loading)
    var uiState by mutableStateOf(TrueFalseQuestionDetailsUiState())
    init {
        getQuestion(trueFalseQuestionId)
    }

    fun setId(id: String){
        trueFalseQuestionId = id
        getQuestion(id)
    }

    fun getQuestion(topicId: String){
        trueFalseDetailsScreenUiState = TrueFalseQuestionDetailsScreenUiState.Loading
        viewModelScope.launch {
            trueFalseDetailsScreenUiState = try{
                val result = ApiService.getTrueFalse(topicId)
                uiState = TrueFalseQuestionDetailsUiState(result.toTrueFalseQuestionDetails(
                    topicName =
                        if (result.topic == "null") ""
                        else ApiService.getTopic(result.topic).topic,
                    pointName =
                        if (result.point == "null") ""
                        else ApiService.getPoint(result.point).type,
                ))
                TrueFalseQuestionDetailsScreenUiState.Success(result)
            } catch (e: IOException) {
                TrueFalseQuestionEditScreenUiState.Error.errorMessage = "Network error"
                TrueFalseQuestionDetailsScreenUiState.Error
            } /*catch (e: HttpException) {
                when(e.code()){
                    400 -> TrueFalseQuestionDetailsScreenUiState.Error.errorMessage = "Bad request"
                    401 -> TrueFalseQuestionDetailsScreenUiState.Error.errorMessage = "Unauthorized try logging in again or open the home screen"
                    404 -> TrueFalseQuestionDetailsScreenUiState.Error.errorMessage = "Content not found"
                    500 -> TrueFalseQuestionDetailsScreenUiState.Error.errorMessage = "Server error"
                    else -> TrueFalseQuestionDetailsScreenUiState.Error
                }
                TrueFalseQuestionDetailsScreenUiState.Error
            }*/
        }
    }

    suspend fun deleteTrueFalseQuestion() {
        try {
            ApiService.deleteTrueFalse(trueFalseQuestionId)
        } catch (e: IOException) {
            TrueFalseQuestionDetailsScreenUiState.Error.errorMessage = "Network error"
            trueFalseDetailsScreenUiState = TrueFalseQuestionDetailsScreenUiState.Error
        } /*catch (e: HttpException) {
            when(e.code()){
                400 -> TrueFalseQuestionDetailsScreenUiState.Error.errorMessage = "Cant delete this question because it is used in an exam"
                401 -> TrueFalseQuestionDetailsScreenUiState.Error.errorMessage = "Unauthorized try logging in again or open the home screen"
                404 -> TrueFalseQuestionDetailsScreenUiState.Error.errorMessage = "Content not found"
                500 -> TrueFalseQuestionDetailsScreenUiState.Error.errorMessage = "Server error"
                else -> TrueFalseQuestionDetailsScreenUiState.Error
            }
            trueFalseDetailsScreenUiState = TrueFalseQuestionDetailsScreenUiState.Error
        }*/
    }

}


data class TrueFalseQuestionDetailsUiState(
    val trueFalseQuestionDetails: TrueFalseQuestionDetails = TrueFalseQuestionDetails()
)