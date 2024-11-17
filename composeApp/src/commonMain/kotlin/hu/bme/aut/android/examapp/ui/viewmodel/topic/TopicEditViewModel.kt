package hu.bme.aut.android.examapp.ui.viewmodel.topic

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


sealed interface TopicEditScreenUiState {
    data class Success(val topic: TopicDto) : TopicEditScreenUiState
    data object Error : TopicEditScreenUiState{var errorMessage: String = ""}
    data object Loading : TopicEditScreenUiState
}

class TopicEditViewModel(
    var topicId: String,
) : ViewModel() {

    private lateinit var originalTopic: String

    val originalTopicPublic get() = originalTopic

    var topicUiState by mutableStateOf(TopicUiState())
        private set


    var topicEditScreenUiState: TopicEditScreenUiState by mutableStateOf(TopicEditScreenUiState.Loading)

    init {
        getTopic(topicId)
    }

    fun setId(id: String){
        topicId = id
        getTopic(id)
    }

    fun getTopic(topicId: String){
        topicEditScreenUiState = TopicEditScreenUiState.Loading
        viewModelScope.launch {
            topicEditScreenUiState = try{
                val result = ApiService.getTopic(topicId)
                topicUiState = result.toTopicUiState(true,
                    parentName =
                    if (result.parentTopic == "null") ""
                    else
                        ApiService.getTopic(result.parentTopic).topic
                )
                originalTopic = topicUiState.topicDetails.topic
                TopicEditScreenUiState.Success(result)
            } catch (e: IOException) {
                TopicEditScreenUiState.Error.errorMessage = "Network error"
                TopicEditScreenUiState.Error
            } catch (e: ApiException) {
                TopicEditScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                TopicEditScreenUiState.Error
            } catch (e: Exception){
                TopicEditScreenUiState.Error.errorMessage = "Network error"
                TopicEditScreenUiState.Error
            }
        }
    }

    suspend fun updateTopic() : Boolean{
        return if (validateInput(topicUiState.topicDetails) && validateUniqueTopic(topicUiState.topicDetails)) {
            try{
                viewModelScope.launch {
                    ApiService.updateTopic(topicUiState.topicDetails.toTopic())
                }
                return true
            } catch (e: IOException) {
                TopicEditScreenUiState.Error.errorMessage = "Network error"
                topicEditScreenUiState = TopicEditScreenUiState.Error
                return false
            } catch (e: ApiException) {
                TopicEditScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                TopicEditScreenUiState.Error
                return false
            } catch (e: Exception){
                TopicEditScreenUiState.Error.errorMessage = "Network error"
                TopicEditScreenUiState.Error
                return false
            }
        }
        else {
            topicUiState = topicUiState.copy(isEntryValid = false)
            false
        }
    }

    fun updateUiState(topicDetails: TopicDetails) {
        topicUiState =
            TopicUiState(topicDetails = topicDetails, isEntryValid = validateInput(topicDetails))
    }

    private fun validateInput(uiState: TopicDetails = topicUiState.topicDetails): Boolean {
        return with(uiState) {
            topic.isNotBlank() && description.isNotBlank() && !topic.contains("/") && !description.contains("/")
        }
    }

    private suspend fun validateUniqueTopic(uiState: TopicDetails = topicUiState.topicDetails): Boolean {
        return try {
            !ApiService.getAllTopicNames().map{it.name}.contains(uiState.topic) || originalTopic == uiState.topic
        } catch (e: IOException) {
            TopicEditScreenUiState.Error.errorMessage = "Network error"
            topicEditScreenUiState = TopicEditScreenUiState.Error
            false
        } catch (e: ApiException) {
            TopicEditScreenUiState.Error.errorMessage = e.message?: "Unkown error"
            TopicEditScreenUiState.Error
            false
        } catch (e: Exception){
            TopicEditScreenUiState.Error.errorMessage = "Network error"
            TopicEditScreenUiState.Error
            false
        }
    }
}