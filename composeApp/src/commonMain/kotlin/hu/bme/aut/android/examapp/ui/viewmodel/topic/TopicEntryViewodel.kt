package hu.bme.aut.android.examapp.ui.viewmodel.topic

import ApiException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.android.examapp.api.dto.TopicDto
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.launch

sealed interface TopicEntryScreenUiState {
    data object Success : TopicEntryScreenUiState
    data object Error : TopicEntryScreenUiState{var errorMessage: String = ""}
    data object Loading : TopicEntryScreenUiState
}

class TopicEntryViewModel: ViewModel(){

    var topicUiState by mutableStateOf(TopicUiState())
        private set

    var topicScreenUiState: TopicEntryScreenUiState by mutableStateOf(TopicEntryScreenUiState.Success)

    fun updateUiState(topicDetails: TopicDetails) {
        topicUiState =
            TopicUiState(topicDetails = topicDetails, isEntryValid = validateInput(topicDetails))
    }

    suspend fun saveTopic() : Boolean {
        return if (validateInput() && validateUniqueTopic()) {
            try{
                viewModelScope.launch {
                    ApiService.postTopic(topicUiState.topicDetails.toTopic())
                }
                true
            } catch (e: IOException){
                TopicEntryScreenUiState.Error.errorMessage = "Network error"
                topicScreenUiState = TopicEntryScreenUiState.Error
                false
            } catch (e: ApiException) {
                TopicEntryScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                topicScreenUiState = TopicEntryScreenUiState.Error
                false
            } catch (e: Exception){
                TopicEntryScreenUiState.Error.errorMessage = "Network error"
                topicScreenUiState = TopicEntryScreenUiState.Error
                false
            }
        } else {
            topicUiState = topicUiState.copy(isEntryValid = false)
            false
        }
    }


    suspend fun getTopicIdByTopic(topic: String): String {
        return try{
            ApiService.getTopicByTopic(topic)?.uuid ?: ""
        } catch (e: IOException) {
            TopicEntryScreenUiState.Error.errorMessage = "Network error"
            topicScreenUiState = TopicEntryScreenUiState.Error
            ""
        } catch (e: ApiException) {
                TopicEntryScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                topicScreenUiState = TopicEntryScreenUiState.Error
                ""
        } catch (e: Exception){
            TopicEntryScreenUiState.Error.errorMessage = "Network error"
            topicScreenUiState = TopicEntryScreenUiState.Error
            ""
        }

    }

    private fun validateInput(uiState: TopicDetails = topicUiState.topicDetails): Boolean {
        return with(uiState) {
            topic.isNotBlank() && description.isNotBlank() && !topic.contains("/") && !description.contains("/")
        }
    }

    private suspend fun validateUniqueTopic(uiState: TopicDetails = topicUiState.topicDetails): Boolean {
        return try{
            !ApiService.getAllTopicNames().map{it.name}.contains(uiState.topic)
        } catch (e: IOException) {
            TopicEntryScreenUiState.Error.errorMessage = "Network error"
            topicScreenUiState = TopicEntryScreenUiState.Error
            false
        } catch (e: ApiException) {
                TopicEntryScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                topicScreenUiState = TopicEntryScreenUiState.Error
                false
        } catch (e: Exception){
            TopicEntryScreenUiState.Error.errorMessage = "Network error"
            topicScreenUiState = TopicEntryScreenUiState.Error
            false
        }
    }

}

data class TopicUiState(
    val topicDetails: TopicDetails = TopicDetails(),
    val isEntryValid: Boolean = false
)

data class TopicDetails(
    val id: String = "",
    val topic: String = "",
    val parent: String = "",
    val description: String = "",
    val parentTopicName : String = ""
)

fun TopicDetails.toTopic(): TopicDto = TopicDto(
    uuid = id,
    topic = topic,
    parentTopic = if(parent == "null") "" else parent,
    description = description,
)

fun TopicDto.toTopicUiState(isEntryValid: Boolean = false, parentName: String): TopicUiState = TopicUiState(
    topicDetails = this.toTopicDetails(parentName),
    isEntryValid = isEntryValid
)

fun TopicDto.toTopicDetails(parentName: String): TopicDetails = TopicDetails(
    id = uuid,
    topic = topic,
    parent = parentTopic,
    description = description,
    parentTopicName = parentName
)