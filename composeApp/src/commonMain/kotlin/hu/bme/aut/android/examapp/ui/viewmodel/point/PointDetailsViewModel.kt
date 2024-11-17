package hu.bme.aut.android.examapp.ui.viewmodel.point

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

sealed interface PointDetailsScreenUiState {
    data class Success(val point: PointDto) : PointDetailsScreenUiState
    data object Error : PointDetailsScreenUiState{var errorMessage: String = ""}
    data object Loading : PointDetailsScreenUiState
}

class PointDetailsViewModel(
    var  pointId : String,
) : ViewModel() {

    var pointDetailsScreenUiState: PointDetailsScreenUiState by mutableStateOf(PointDetailsScreenUiState.Loading)

    init {
        getPoint(pointId)
    }

    fun setId(id: String){
        pointId = id
        getPoint(id)
    }

    fun getPoint(pointId: String){
        pointDetailsScreenUiState = PointDetailsScreenUiState.Loading
        viewModelScope.launch {
            pointDetailsScreenUiState = try{
                val result = ApiService.getPoint(pointId)
                PointDetailsScreenUiState.Success(result)
            } catch (e: IOException) {
                PointDetailsScreenUiState.Error
            } catch (e: ApiException) {
                PointDetailsScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                PointDetailsScreenUiState.Error
            } catch (e: Exception){
                PointDetailsScreenUiState.Error.errorMessage = "Network error"
                PointDetailsScreenUiState.Error
            }
        }
    }

    suspend fun deletePoint() {
        try{
            viewModelScope.launch {
                ApiService.deletePoint(pointId)
            }
        } catch (e: IOException) {
            PointDetailsScreenUiState.Error.errorMessage = "Network error"
            pointDetailsScreenUiState = PointDetailsScreenUiState.Error
        } catch (e: ApiException) {
                PointDetailsScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                PointDetailsScreenUiState.Error
        } catch (e: Exception){
            PointDetailsScreenUiState.Error.errorMessage = "Network error"
            PointDetailsScreenUiState.Error
        }

    }

}

data class PointDetailsUiState(
    val pointDetails: PointDetails = PointDetails()
)