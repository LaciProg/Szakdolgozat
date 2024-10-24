package hu.bme.aut.android.examapp.ui.viewmodel.point

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.android.examapp.api.dto.NameDto
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
            } /*catch (e: HttpException) {
                when(e.code()){
                    400 -> PointListScreenUiState.Error.errorMessage = "Bad request"
                    401 -> PointListScreenUiState.Error.errorMessage = "Unauthorized try logging in again or open the home screen"
                    404 -> PointListScreenUiState.Error.errorMessage = "Content not found"
                    500 -> PointListScreenUiState.Error.errorMessage = "Server error"
                    else -> PointListScreenUiState.Error
                }
                PointListScreenUiState.Error
            }*/
        }
    }

}

data class PointListUiState(val pointList: List<PointRowUiState> = listOf(PointRowUiState()))

data class PointRowUiState(
    val point: String = "",
    val id: String = ""
)