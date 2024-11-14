package hu.bme.aut.android.examapp.ui.viewmodel.point

import ApiException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.android.examapp.api.dto.PointDto
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface PointEntryScreenUiState {
    data object Success : PointEntryScreenUiState
    data object Error : PointEntryScreenUiState{var errorMessage: String = ""}
    data object Loading : PointEntryScreenUiState
}

class PointEntryViewModel: ViewModel(){

    var pointUiState by mutableStateOf(PointUiState())
        private set

    var pointScreenUiState: PointEntryScreenUiState by mutableStateOf(PointEntryScreenUiState.Success)

    fun updateUiState(pointDetails: PointDetails) {
        pointUiState =
            PointUiState(pointDetails = pointDetails, isEntryValid = validateInput(pointDetails))
    }

    suspend fun savePoint() : Boolean {
        return if (validateInput() && validateUniqueTopic()) {
            try{
                viewModelScope.launch {
                    ApiService.postPoint(pointUiState.pointDetails.toPoint())
                }
                true
            } catch (e: IOException){
                PointEntryScreenUiState.Error.errorMessage = "Network error"
                pointScreenUiState = PointEntryScreenUiState.Error
                false
            }  catch (e: ApiException) {
                PointEntryScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                pointScreenUiState = PointEntryScreenUiState.Error
                false
            } catch (e: Exception){
                PointEntryScreenUiState.Error.errorMessage = "Network error"
                pointScreenUiState = PointEntryScreenUiState.Error
                false
            }
        }
        else{
            pointUiState = pointUiState.copy(isEntryValid = false)
            false
        }
    }

    private fun validateInput(uiState: PointDetails = pointUiState.pointDetails): Boolean {
        return with(uiState) {
            type.isNotBlank() && point.isNotBlank() && goodAnswer.isNotBlank() && badAnswer.isNotBlank()
        }
    }

    private suspend fun validateUniqueTopic(uiState: PointDetails = pointUiState.pointDetails): Boolean {
        return try{
            !ApiService.getAllPointNames().map{it.name}.contains(uiState.type)
        } catch (e: IOException) {
            PointEntryScreenUiState.Error.errorMessage = "Network error"
            pointScreenUiState = PointEntryScreenUiState.Error
            false
        } catch (e: ApiException) {
                PointEntryScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                pointScreenUiState = PointEntryScreenUiState.Error
                false
        } catch (e: Exception){
            PointEntryScreenUiState.Error.errorMessage = "Network error"
            pointScreenUiState = PointEntryScreenUiState.Error
            false
        }
    }

}

data class PointUiState(
    val pointDetails: PointDetails = PointDetails(),
    val isEntryValid: Boolean = false
)

data class PointDetails(
    val id: String = "",
    val point: String = "0",
    val type: String = "",
    val goodAnswer: String = "0",
    val badAnswer: String = "0"
)

fun PointDetails.toPoint(): PointDto = PointDto(
    uuid = id,
    point = point.toDouble(),
    type = type,
    goodAnswer = goodAnswer.toDouble(),
    badAnswer = badAnswer.toDouble()
)

fun PointDto.toPointUiState(isEntryValid: Boolean = false): PointUiState = PointUiState(
    pointDetails = this.toPointDetails(),
    isEntryValid = isEntryValid
)

fun PointDto.toPointDetails(): PointDetails = PointDetails(
    id = uuid,
    point = point.toString(),
    type = type,
    goodAnswer = goodAnswer.toString(),
    badAnswer = badAnswer.toString()
)