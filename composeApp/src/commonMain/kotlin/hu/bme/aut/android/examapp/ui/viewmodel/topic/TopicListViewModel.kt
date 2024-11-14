package hu.bme.aut.android.examapp.ui.viewmodel.topic

import ApiException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.android.examapp.api.dto.NameDto
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.launch
import io.ktor.http.*
//import retrofit2.HttpException
//import javax.inject.Inject

sealed interface TopicListScreenUiState {
    data class Success(val topics: List<NameDto>) : TopicListScreenUiState
    data object Error : TopicListScreenUiState{var errorMessage: String = ""}
    data object Loading : TopicListScreenUiState
}

class TopicListViewModel: ViewModel() {
    var topicListScreenUiState: TopicListScreenUiState by mutableStateOf(TopicListScreenUiState.Loading)
    var topicListUiState: TopicListUiState by mutableStateOf(TopicListUiState())
    init {
        getAllTopicList()
    }

    fun getAllTopicList(){
        topicListScreenUiState = TopicListScreenUiState.Loading
        viewModelScope.launch {
            topicListScreenUiState = try{
                val result = ApiService.getAllTopicNames()
                topicListUiState = TopicListUiState(
                    topicList = result.map { nameDto ->
                        TopicRowUiState(
                            topic = nameDto.name,
                            id = nameDto.uuid
                        )
                    }
                )
                TopicListScreenUiState.Success(result)
            } catch (e: IOException) {
                TopicListScreenUiState.Error.errorMessage = e.toString()// "Network error"
                TopicListScreenUiState.Error
            } catch (e: ApiException) {
                TopicListScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                TopicListScreenUiState.Error
            } catch (e: Exception){
                TopicListScreenUiState.Error.errorMessage = "Network error"
                TopicListScreenUiState.Error
            }
        }
    }

}

data class TopicListUiState(val topicList: List<TopicRowUiState> = listOf(TopicRowUiState()))

data class TopicRowUiState(
    val topic: String = "",
    val id: String = ""
)