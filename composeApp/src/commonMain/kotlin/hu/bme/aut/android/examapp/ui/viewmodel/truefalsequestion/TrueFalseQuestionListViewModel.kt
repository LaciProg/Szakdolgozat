package hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.android.examapp.api.dto.NameDto
import kotlinx.coroutines.launch
import java.io.IOException


sealed interface TrueFalseQuestionListScreenUiState {
    data class Success(val questions: List<NameDto>) : TrueFalseQuestionListScreenUiState
    data object Error : TrueFalseQuestionListScreenUiState{var errorMessage: String = ""}
    data object Loading : TrueFalseQuestionListScreenUiState
}

class TrueFalseQuestionListViewModel: ViewModel() {

    var trueFalseQuestionListScreenUiState: TrueFalseQuestionListScreenUiState by mutableStateOf(TrueFalseQuestionListScreenUiState.Loading)
    var trueFalseQuestionListUiState: TrueFalseQuestionListUiState by mutableStateOf(TrueFalseQuestionListUiState())


    init {
        getAllTrueFalseQuestionList()
    }

    fun getAllTrueFalseQuestionList(){
        trueFalseQuestionListScreenUiState = TrueFalseQuestionListScreenUiState.Loading
        viewModelScope.launch {
            trueFalseQuestionListScreenUiState = try{
                val result = ApiService.getAllTrueFalseNames()
                trueFalseQuestionListUiState = TrueFalseQuestionListUiState(
                    trueFalseQuestionList = result.map { nameDto ->
                        TrueFalseQuestionRowUiState(
                            id = nameDto.uuid,
                            question = nameDto.name,
                        )
                    }
                )
                TrueFalseQuestionListScreenUiState.Success(result)
            } catch (e: IOException) {
                TrueFalseQuestionListScreenUiState.Error
            } /*catch (e: HttpException) {
                when(e.code()){
                    400 -> TrueFalseQuestionListScreenUiState.Error.errorMessage = "Bad request"
                    401 -> TrueFalseQuestionListScreenUiState.Error.errorMessage = "Unauthorized try logging in again or open the home screen"
                    404 -> TrueFalseQuestionListScreenUiState.Error.errorMessage = "Content not found"
                    500 -> TrueFalseQuestionListScreenUiState.Error.errorMessage = "Server error"
                    else -> TrueFalseQuestionListScreenUiState.Error
                }
                TrueFalseQuestionListScreenUiState.Error
            }*/
        }
    }

}

data class TrueFalseQuestionListUiState(val trueFalseQuestionList: List<TrueFalseQuestionRowUiState> = listOf(
    TrueFalseQuestionRowUiState("",""),
))

data class TrueFalseQuestionRowUiState(
    val question: String = "",
    val id: String = ""
)