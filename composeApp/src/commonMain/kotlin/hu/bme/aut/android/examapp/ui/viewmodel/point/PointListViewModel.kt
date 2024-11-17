package hu.bme.aut.android.examapp.ui.viewmodel.point

import ApiException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.android.examapp.service.api.dto.*
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface PointListScreenUiState {
    data class Success(val points: List<NameDto>) : PointListScreenUiState
    data object Error : PointListScreenUiState{var errorMessage: String = ""}
    data object Loading : PointListScreenUiState
}

class PointListViewModel: ViewModel() {

    var pointListScreenUiState: PointListScreenUiState by mutableStateOf(PointListScreenUiState.Loading)
    var pointListUiState: PointListUiState by mutableStateOf(PointListUiState())
    init {
        getAllPointList()
    }

    fun getAllPointList(){
        pointListScreenUiState = PointListScreenUiState.Loading
        viewModelScope.launch {
            pointListScreenUiState = try{
                val result = ApiService.getAllPointNames()
                pointListUiState = PointListUiState(
                    pointList = result.map { nameDto ->
                        PointRowUiState(
                            point = nameDto.name,
                            id = nameDto.uuid
                        )
                    }
                )
                PointListScreenUiState.Success(result)
            } catch (e: IOException) {
                PointListScreenUiState.Error
            } catch (e: ApiException) {
                PointListScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                PointListScreenUiState.Error
            } catch (e: Exception){
                PointListScreenUiState.Error.errorMessage = "Network error"
                PointListScreenUiState.Error
            }
        }
    }

}

data class PointListUiState(val pointList: List<PointRowUiState> = listOf(PointRowUiState()))

data class PointRowUiState(
    val point: String = "",
    val id: String = ""
)