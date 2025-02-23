package hu.bme.aut.android.examapp.ui.viewmodel.exam

import ApiException
import androidx.lifecycle.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import hu.bme.aut.android.examapp.service.api.dto.*
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface ExamListScreenUiState {
    data class Success(val exams: List<NameDto>) : ExamListScreenUiState
    data object Error : ExamListScreenUiState {var errorMessage: String = ""}
    data object Loading : ExamListScreenUiState
}

class ExamListViewModel: ViewModel() {

    var examListScreenUiState: ExamListScreenUiState by mutableStateOf(
        ExamListScreenUiState.Loading
    )
    var examListUiState: ExamListUiState by mutableStateOf(
        ExamListUiState()
    )


    init {
        getAllExamList()

    }

    fun getAllExamList(){
        examListScreenUiState = ExamListScreenUiState.Loading
        viewModelScope.launch {
            examListScreenUiState = try{
                val result = ApiService.getAllExamNames()
                examListUiState = ExamListUiState(
                    examList = result.map { nameDto ->
                        ExamRowUiState(
                            id = nameDto.uuid,
                            exam = nameDto.name,
                        )
                    }
                )
                ExamListScreenUiState.Success(result)
            } catch (e: IOException) {
                ExamListScreenUiState.Error
            } catch (e: ApiException) {
                ExamListScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                ExamListScreenUiState.Error
            } catch (e: Exception){
                ExamListScreenUiState.Error.errorMessage = "Network error"
                ExamListScreenUiState.Error
            }
        }
    }

}

data class ExamListUiState(val examList: List<ExamRowUiState> = listOf(ExamRowUiState()))

data class ExamRowUiState(
    val exam: String = "",
    val id: String = ""
)