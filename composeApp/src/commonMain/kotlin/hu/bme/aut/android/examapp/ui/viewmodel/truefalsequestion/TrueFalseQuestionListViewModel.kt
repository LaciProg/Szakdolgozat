package hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion

import ApiException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.android.examapp.service.api.dto.*
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
            } catch (e: ApiException) {
                TrueFalseQuestionListScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                TrueFalseQuestionListScreenUiState.Error
            } catch (e: Exception){
                TrueFalseQuestionListScreenUiState.Error.errorMessage = "Network error"
                TrueFalseQuestionListScreenUiState.Error
            }
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