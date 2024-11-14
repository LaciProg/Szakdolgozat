package hu.bme.aut.android.examapp.ui.viewmodel.multiplechoicequestion

import ApiException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.android.examapp.api.dto.NameDto
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.launch

sealed interface MultipleChoiceQuestionListScreenUiState {
    data class Success(val questions: List<NameDto>) : MultipleChoiceQuestionListScreenUiState
    data object Error : MultipleChoiceQuestionListScreenUiState{var errorMessage: String = ""}
    data object Loading : MultipleChoiceQuestionListScreenUiState
}

class MultipleChoiceQuestionListViewModel: ViewModel() {

    var multipleChoiceQuestionListScreenUiState: MultipleChoiceQuestionListScreenUiState by mutableStateOf(
        MultipleChoiceQuestionListScreenUiState.Loading)
    var multipleChoiceQuestionListUiState: MultipleChoiceQuestionListUiState by mutableStateOf(
        MultipleChoiceQuestionListUiState()
    )


    init {
        getAllMultipleChoiceQuestionList()
    }

    fun getAllMultipleChoiceQuestionList(){
        multipleChoiceQuestionListScreenUiState = MultipleChoiceQuestionListScreenUiState.Loading
        viewModelScope.launch {
            multipleChoiceQuestionListScreenUiState = try{
                val result = ApiService.getAllMultipleChoiceNames()
                multipleChoiceQuestionListUiState = MultipleChoiceQuestionListUiState(
                    multipleChoiceQuestionList = result.map { nameDto ->
                        MultipleChoiceQuestionRowUiState(
                            id = nameDto.uuid,
                            question = nameDto.name,
                        )
                    }
                )
                MultipleChoiceQuestionListScreenUiState.Success(result)
            } catch (e: IOException) {
                MultipleChoiceQuestionListScreenUiState.Error
            } catch (e: ApiException) {
                MultipleChoiceQuestionListScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                MultipleChoiceQuestionListScreenUiState.Error
            } catch (e: Exception){
                MultipleChoiceQuestionListScreenUiState.Error.errorMessage = "Network error"
                MultipleChoiceQuestionListScreenUiState.Error
            }
        }
    }

}

data class MultipleChoiceQuestionListUiState(val multipleChoiceQuestionList: List<MultipleChoiceQuestionRowUiState> = listOf(
    MultipleChoiceQuestionRowUiState("",  ""),
))

data class MultipleChoiceQuestionRowUiState(
    val question: String = "",
    val id: String = ""
)