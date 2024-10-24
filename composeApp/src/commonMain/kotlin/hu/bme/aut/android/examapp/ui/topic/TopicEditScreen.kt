package hu.bme.aut.android.examapp.ui.topic

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.bme.aut.android.examapp.Notify
import hu.bme.aut.android.examapp.ui.viewmodel.topic.TopicEditScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.topic.TopicEditViewModel
import kotlinx.coroutines.launch

@Composable
fun TopicEditScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    savedStateHandle: SavedStateHandle,
    viewModel: TopicEditViewModel = viewModel { TopicEditViewModel(savedStateHandle) },
    //viewModel: TopicEditViewModel = hiltViewModel()//viewModel(factory = AppViewModelProvider.Factory)
) {

    when (viewModel.topicEditScreenUiState) {
        is TopicEditScreenUiState.Loading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        is TopicEditScreenUiState.Success -> TopicEditResultScreen(
            navigateBack = navigateBack,
            viewModel = viewModel,
            modifier = modifier
        )
        is TopicEditScreenUiState.Error -> Text(text = TopicEditScreenUiState.Error.errorMessage.ifBlank { "Unexpected error " })
    }
}

@Composable
fun TopicEditResultScreen(
    navigateBack: () -> Unit,
    viewModel: TopicEditViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var showNotify by remember { mutableStateOf(false) }
    var notifyMessage by remember { mutableStateOf("") }

    if (showNotify) {
        Notify(notifyMessage)
        showNotify = false
    }

    TopicEntryBody(
        topicUiState = viewModel.topicUiState,
        onTopicValueChange = viewModel::updateUiState,
        onSaveClick = {
            coroutineScope.launch {
                if(viewModel.updateTopic()){
                    navigateBack()
                }
                else{
                    showNotify = true
                    notifyMessage = "Topic with this name already exists"
                }
            }
        },
        originalTopic = viewModel.originalTopicPublic,
        modifier = modifier
    )
}