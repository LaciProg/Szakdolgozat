package hu.bme.aut.android.examapp.ui.topic

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import examapp.composeapp.generated.resources.Res
import examapp.composeapp.generated.resources.*
import hu.bme.aut.android.examapp.api.dto.TopicDto
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.viewmodel.topic.TopicDetails
import hu.bme.aut.android.examapp.ui.viewmodel.topic.TopicDetailsScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.topic.TopicDetailsUiState
import hu.bme.aut.android.examapp.ui.viewmodel.topic.TopicDetailsViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopicDetailsScreen(
    navigateToEditTopic: (String) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    //savedStateHandle: SavedStateHandle,
    topicId: String,
    viewModel: TopicDetailsViewModel = viewModel { TopicDetailsViewModel(topicId) },
) {
    when(viewModel.topicDetailsScreenUiState){
        is TopicDetailsScreenUiState.Loading -> CircularProgressIndicator()
        is TopicDetailsScreenUiState.Success -> TopicDetailsScreenUiState(
            topic =  (viewModel.topicDetailsScreenUiState as TopicDetailsScreenUiState.Success).point,
            navigateToEditPoint = navigateToEditTopic,
            navigateBack = navigateBack,
            modifier = modifier,
            viewModel = viewModel
        )
        is TopicDetailsScreenUiState.Error -> Text(text = TopicDetailsScreenUiState.Error.errorMessage.ifBlank { "Unexpected error " })
    }

    LaunchedEffect(key1 = Unit){
        viewModel.getTopic(viewModel.topicId)
    }
}

@Composable
fun TopicDetailsScreenUiState(
    topic: TopicDto,
    navigateToEditPoint: (String) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TopicDetailsViewModel
){
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = { TopAppBarContent(stringResource(Res.string.topic_details), navigateBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEditPoint(topic.uuid) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(20.dp)

            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(Res.string.edit_topic),
                )
            }
        }, modifier = modifier
    ) { innerPadding ->
        TopicDetailsBody(
            topicDetailsUiState = viewModel.uiState,
            onDelete = {
                coroutineScope.launch {
                    viewModel.deleteTopic()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
private fun TopicDetailsBody(
    topicDetailsUiState: TopicDetailsUiState,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
        TopicDetails(
            topic = topicDetailsUiState.topicDetails, modifier = Modifier.fillMaxWidth()
        )
        OutlinedButton(
            onClick = { deleteConfirmationRequired = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.delete))
        }
        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onDelete()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}


@Composable
fun TopicDetails(
    topic: TopicDetails,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TopicDetailsRow(
                labelRes = Res.string.topic,
                topicDetail = topic.topic,
                modifier = Modifier.padding(
                    horizontal = 16.dp
                )
            )
            TopicDetailsRow(
                labelRes = Res.string.description,
                topicDetail = topic.description,
                modifier = Modifier.padding(
                    horizontal = 16.dp
                )
            )
            TopicDetailsRow(
                labelRes = Res.string.parent_topic,
                topicDetail = if(topic.parent == "null") stringResource(Res.string.no_parent) else topic.parentTopicName,
                modifier = Modifier.padding(
                    horizontal = 16.dp
                )
            )
        }

    }
}

@Composable
private fun TopicDetailsRow(
    @StringRes labelRes: StringResource, topicDetail: String, modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(text = stringResource(labelRes), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = topicDetail, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit, onDeleteCancel: () -> Unit, modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(Res.string.attention)) },
        text = { Text(stringResource(Res.string.delete_question)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = stringResource(Res.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = stringResource(Res.string.yes))
            }
        })
}