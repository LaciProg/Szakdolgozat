package hu.bme.aut.android.examapp.ui.viewmodel.exam

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.android.examapp.api.dto.ExamDto
import hu.bme.aut.android.examapp.navigation.ExamDestination
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface ExamEditScreenUiState {
    data class Success(val question: ExamDto) : ExamEditScreenUiState
    data object Error : ExamEditScreenUiState{var errorMessage: String = ""}
    data object Loading : ExamEditScreenUiState
}

class ExamEditViewModel(
    var examId: String,
) : ViewModel() {

    private lateinit var originalExam: String

    var examUiState by mutableStateOf(ExamUiState())
        private set

    var examEditScreenUiState: ExamEditScreenUiState by mutableStateOf(
        ExamEditScreenUiState.Loading)

    init {
        getExam(examId)
    }

    fun setId(id: String){
        examId = id
        getExam(id)
    }

    fun getExam(topicId: String){
        examEditScreenUiState = ExamEditScreenUiState.Loading
        viewModelScope.launch {
            examEditScreenUiState =  try{
                val result = ApiService.getExam(topicId)
                examUiState = result.toExamUiState(isEntryValid = true,
                    topicName =
                    if (result.topicId == "null") ""
                    else ApiService.getTopic(result.topicId).topic,
                    questionList = result.questionList
                )
                originalExam = examUiState.examDetails.name
                ExamEditScreenUiState.Success(result)
            } catch (e: IOException) {
                ExamEditScreenUiState.Error
            } /*catch (e: HttpException) {
                when(e.code()){
                    400 -> ExamEditScreenUiState.Error.errorMessage = "Bad request"
                    401 -> ExamEditScreenUiState.Error.errorMessage = "Unauthorized try logging in again or open the home screen"
                    404 -> ExamEditScreenUiState.Error.errorMessage = "Content not found"
                    500 -> ExamEditScreenUiState.Error.errorMessage = "Server error"
                    else -> ExamEditScreenUiState.Error
                }
                ExamEditScreenUiState.Error
            }*/ catch (e: IllegalArgumentException){
                ExamEditScreenUiState.Error.errorMessage = "Server or connection error"
                ExamEditScreenUiState.Error
            }
        }
    }

    suspend fun updateExam() : Boolean{
        return if (validateInput(examUiState.examDetails) && validateUniqueExam(examUiState.examDetails)) {
            try {
                ApiService.updateExam(examUiState.examDetails.toExam())
                true
            } catch (e: IOException) {
                ExamEditScreenUiState.Error.errorMessage = "Network error"
                examEditScreenUiState = ExamEditScreenUiState.Error
                false
            } /*catch (e: HttpException) {
                when(e.code()){
                    400 -> ExamEditScreenUiState.Error.errorMessage = "Bad request"
                    401 -> ExamEditScreenUiState.Error.errorMessage = "Unauthorized try logging in again or open the home screen"
                    404 -> ExamEditScreenUiState.Error.errorMessage = "Content not found"
                    500 -> ExamEditScreenUiState.Error.errorMessage = "Server error"
                    else -> ExamEditScreenUiState.Error
                }
                examEditScreenUiState = ExamEditScreenUiState.Error
                false
            }*/
        }
        else {
            examUiState = examUiState.copy(isEntryValid = false)
            false
        }
    }

    fun updateUiState(examDetails: ExamDetails) {
        examUiState =
            ExamUiState(examDetails = examDetails, isEntryValid = validateInput(examDetails))
    }

    private fun validateInput(uiState: ExamDetails = examUiState.examDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && topicId != ""
        }
    }

    private suspend fun validateUniqueExam(uiState: ExamDetails = examUiState.examDetails): Boolean {
        return try{
            !ApiService.getAllExamNames().map{ it.name }.contains(uiState.name) || originalExam == uiState.name
        } catch (e: IOException) {
            ExamEditScreenUiState.Error.errorMessage = "Network error"
            examEditScreenUiState = ExamEditScreenUiState.Error
            false
        } /*catch (e: HttpException) {
            when(e.code()){
                400 -> ExamEditScreenUiState.Error.errorMessage = "Bad request"
                401 -> ExamEditScreenUiState.Error.errorMessage = "Unauthorized try logging in again or open the home screen"
                404 -> ExamEditScreenUiState.Error.errorMessage = "Content not found"
                500 -> ExamEditScreenUiState.Error.errorMessage = "Server error"
                else -> ExamEditScreenUiState.Error
            }
            examEditScreenUiState = ExamEditScreenUiState.Error
            false
        }*/
    }
}



