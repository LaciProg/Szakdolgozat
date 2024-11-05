package hu.bme.aut.android.examapp.ui.topic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import examapp.composeapp.generated.resources.Res
import examapp.composeapp.generated.resources.topic_edit
import examapp.composeapp.generated.resources.topic_list
import hu.bme.aut.android.examapp.Notify
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.viewmodel.topic.TopicEditScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.topic.TopicEditViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopicEditScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    topicId: String,
    viewModel: TopicEditViewModel = viewModel { TopicEditViewModel(topicId) },
) {
    LaunchedEffect(topicId) {
        viewModel.setId(topicId)
    }
    when (viewModel.topicEditScreenUiState) {
        is TopicEditScreenUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
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

    Scaffold(
        topBar = { TopAppBarContent(stringResource(Res.string.topic_edit), navigateBack) },
        ){ innerPadding ->
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
                modifier = modifier.padding(innerPadding)
            )
        }

}