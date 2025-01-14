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

sealed interface PointEditScreenUiState {
    data class Success(val point: PointDto) : PointEditScreenUiState
    data object Error : PointEditScreenUiState{var errorMessage: String = ""}
    data object Loading : PointEditScreenUiState
}

class PointEditViewModel(
    var pointId: String,
) : ViewModel() {

    var pointUiState by mutableStateOf(PointUiState())
        private set

    private lateinit var originalPoint: String

    var pointEditScreenUiState: PointEditScreenUiState by mutableStateOf(PointEditScreenUiState.Loading)

    init {
        getPoint(pointId)
    }

    fun setId(id: String){
        pointId = id
        getPoint(id)
    }

    fun getPoint(pointId: String){
        pointEditScreenUiState = PointEditScreenUiState.Loading
        viewModelScope.launch {
            pointEditScreenUiState = try{
                val result = ApiService.getPoint(pointId)
                pointUiState = result.toPointUiState(true)
                originalPoint = pointUiState.pointDetails.type
                PointEditScreenUiState.Success(result)
            } catch (e: IOException) {
                PointEditScreenUiState.Error
            } catch (e: ApiException) {
                PointEditScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                PointEditScreenUiState.Error
            } catch (e: Exception){
                PointEditScreenUiState.Error.errorMessage = "Network error"
                PointEditScreenUiState.Error
            }
        }
    }

    suspend fun updatePoint() : Boolean{
        return if (validateInput(pointUiState.pointDetails) && validateUniquePoint(pointUiState.pointDetails)) {
            try {
                viewModelScope.launch {
                    ApiService.updatePoint(pointUiState.pointDetails.toPoint())
                }
                return true
            } catch (e: IOException) {
                PointEditScreenUiState.Error.errorMessage = "Network error"
                pointEditScreenUiState = PointEditScreenUiState.Error
                return false
            } catch (e: ApiException) {
                PointEditScreenUiState.Error.errorMessage = e.message?: "Unkown error"
               pointEditScreenUiState = PointEditScreenUiState.Error
                return false
            } catch (e: Exception){
                PointEditScreenUiState.Error.errorMessage = "Network error"
                pointEditScreenUiState = PointEditScreenUiState.Error
                return false
            }
        }
        else{
            pointUiState = pointUiState.copy(isEntryValid = false)
            false
        }
    }


    fun updateUiState(pointDetails: PointDetails) {
        pointUiState =
            PointUiState(pointDetails = pointDetails, isEntryValid = validateInput(pointDetails))
    }

    private fun validateInput(uiState: PointDetails = pointUiState.pointDetails): Boolean {
        return with(uiState) {
            point.isNotBlank() && type.isNotBlank() && goodAnswer.isNotBlank() && badAnswer.isNotBlank() && !type.contains("/")
        }
    }

    private suspend fun validateUniquePoint(uiState: PointDetails = pointUiState.pointDetails): Boolean {
        return try {
            !ApiService.getAllPointNames().map { it.name }.contains(uiState.type) || originalPoint == uiState.type
        } catch (e: IOException) {
            PointEditScreenUiState.Error.errorMessage = "Network error"
            pointEditScreenUiState = PointEditScreenUiState.Error
            false
        } catch (e: ApiException) {
            PointEditScreenUiState.Error.errorMessage = e.message?: "Unkown error"
            pointEditScreenUiState = PointEditScreenUiState.Error
            false
        } catch (e: Exception){
            PointEditScreenUiState.Error.errorMessage = "Network error"
            pointEditScreenUiState = PointEditScreenUiState.Error
            false
        }
    }
}