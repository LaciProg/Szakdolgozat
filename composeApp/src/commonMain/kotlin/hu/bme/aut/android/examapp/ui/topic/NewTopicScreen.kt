package hu.bme.aut.android.examapp.ui.topic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import examapp.composeapp.generated.resources.Res
import examapp.composeapp.generated.resources.*
import hu.bme.aut.android.examapp.Notify
import hu.bme.aut.android.examapp.ui.components.DropDownList
import hu.bme.aut.android.examapp.ui.viewmodel.topic.TopicDetails
import hu.bme.aut.android.examapp.ui.viewmodel.topic.TopicEntryScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.topic.TopicEntryViewModel
import hu.bme.aut.android.examapp.ui.viewmodel.topic.TopicListViewModel
import hu.bme.aut.android.examapp.ui.viewmodel.topic.TopicUiState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewTopic(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: TopicEntryViewModel = viewModel { TopicEntryViewModel() },
    //viewModel: TopicEntryViewModel = hiltViewModel()//viewModel(factory = AppViewModelProvider.Factory)
) {
    when(viewModel.topicScreenUiState){
        TopicEntryScreenUiState.Loading -> CircularProgressIndicator(modifier = Modifier.fillMaxWidth())
        TopicEntryScreenUiState.Success -> NewTopicScreenUiState(viewModel, navigateBack)
        TopicEntryScreenUiState.Error -> Text(text = TopicEntryScreenUiState.Error.errorMessage.ifBlank { "Unexpected error " })
    }

}

@Composable
private fun NewTopicScreenUiState(
    viewModel: TopicEntryViewModel,
    navigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var showNotify by remember { mutableStateOf(false) }
    var notifyMessage by remember { mutableStateOf("") }

    if (showNotify) {
        Notify(notifyMessage)
        showNotify = false
    }

    Scaffold(
        topBar = { }
    ) { innerPadding ->
        TopicEntryBody(
            topicUiState = viewModel.topicUiState,
            onTopicValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    if (viewModel.saveTopic()) {
                        navigateBack()
                    } else {
                        showNotify = true
                        notifyMessage = "Topic with this name already exists"
                    }
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun TopicEntryBody(
    topicUiState: TopicUiState,
    onTopicValueChange: (TopicDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    originalTopic: String = "",
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.padding(16.dp)
    ) {
        TopicInputForm(
            topicDetails = topicUiState.topicDetails,
            onValueChange = onTopicValueChange,
            originalTopic = originalTopic,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = topicUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.save_action))
        }
    }
}

@Composable
fun TopicInputForm(
    topicDetails: TopicDetails,
    modifier: Modifier = Modifier,
    onValueChange: (TopicDetails) -> Unit = {},
    default: String = "",
    enabled: Boolean = true,
    originalTopic: String = "",
    listViewModel: TopicListViewModel = viewModel { TopicListViewModel() },
    //listViewModel: TopicListViewModel = hiltViewModel(),//viewModel(factory = AppViewModelProvider.Factory),
    entryViewModel: TopicEntryViewModel = viewModel { TopicEntryViewModel() },
    //entryViewModel: TopicEntryViewModel = hiltViewModel()//viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = topicDetails.topic,
            onValueChange = { onValueChange(topicDetails.copy(topic = it)) },
            label = { Text(stringResource(Res.string.topic_name_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = topicDetails.description,
            onValueChange = { onValueChange(topicDetails.copy(description = it)) },
            label = { Text(stringResource(Res.string.topic_description_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        DropDownList(
            name = stringResource(Res.string.topic),
            items = listViewModel.topicListUiState.topicList.map { it.topic } .filterNot{ it == originalTopic },
            onChoose = {parent ->
                coroutineScope.launch{
                    onValueChange(topicDetails.copy(parent = entryViewModel.getTopicIdByTopic(parent)))
                }
            },
            default = topicDetails.parentTopicName,
            modifier = Modifier.fillMaxWidth(),
        )
        if (enabled) {
            Text(
                text = stringResource(Res.string.required_fields),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}