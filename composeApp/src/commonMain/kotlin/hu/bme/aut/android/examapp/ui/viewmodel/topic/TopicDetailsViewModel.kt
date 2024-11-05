package hu.bme.aut.android.examapp.ui.viewmodel.topic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.android.examapp.api.dto.TopicDto
import hu.bme.aut.android.examapp.navigation.ExamDestination
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.launch


sealed interface TopicDetailsScreenUiState {
    data class Success(val point: TopicDto) : TopicDetailsScreenUiState
    data object Error : TopicDetailsScreenUiState{var errorMessage: String = ""}
    data object Loading : TopicDetailsScreenUiState
}

class TopicDetailsViewModel(
    var topicId: String,
) : ViewModel() {

    var topicDetailsScreenUiState: TopicDetailsScreenUiState by mutableStateOf(TopicDetailsScreenUiState.Loading)
    var uiState by mutableStateOf(TopicDetailsUiState())
    init {
        getTopic(topicId)
    }

    fun setId(id: String){
        topicId = id
        getTopic(id)
    }

    fun getTopic(topicId: String){
        topicDetailsScreenUiState = TopicDetailsScreenUiState.Loading
        viewModelScope.launch {
            topicDetailsScreenUiState = try{
                val result = ApiService.getTopic(topicId)
                uiState = TopicDetailsUiState(result.toTopicDetails(
                    parentName =
                    if (result.parentTopic == "null") ""
                    else
                        ApiService.getTopic(result.parentTopic).topic
                ))
            TopicDetailsScreenUiState.Success(result)
            } catch (e: IOException) {
                TopicDetailsScreenUiState.Error.errorMessage = "Network error"
                TopicDetailsScreenUiState.Error
            } /*catch (e: HttpException) {
                when(e.code()){
                    400 -> TopicDetailsScreenUiState.Error.errorMessage = "You can't delete a topic because it is used in a question, exam or topic."
                    401 -> TopicDetailsScreenUiState.Error.errorMessage = "Unauthorized try logging in again or open the home screen"
                    404 -> TopicDetailsScreenUiState.Error.errorMessage = "Content not found"
                    500 -> TopicDetailsScreenUiState.Error.errorMessage = "Server error"
                    else -> TopicDetailsScreenUiState.Error
                }
                TopicDetailsScreenUiState.Error
            }*/
        }
    }

    suspend fun deleteTopic() {
        try {
            ApiService.deleteTopic(topicId)
        } catch (e: IOException){
            TopicDetailsScreenUiState.Error.errorMessage = "Network error"
            topicDetailsScreenUiState = TopicDetailsScreenUiState.Error
        } /*catch (e: HttpException){
            when(e.code()){
                400 -> TopicDetailsScreenUiState.Error.errorMessage = "Bad request"
                401 -> TopicDetailsScreenUiState.Error.errorMessage = "Unauthorized try logging in again or open the home screen"
                404 -> TopicDetailsScreenUiState.Error.errorMessage = "Content not found"
                500 -> TopicDetailsScreenUiState.Error.errorMessage = "Server error"
                else -> TopicDetailsScreenUiState.Error
            }
            topicDetailsScreenUiState = TopicDetailsScreenUiState.Error
        }*/
    }

}

data class TopicDetailsUiState(
    val topicDetails: TopicDetails = TopicDetails()
)