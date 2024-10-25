package hu.bme.aut.android.examapp.ui.exam

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
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamDetails
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamEntryScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamEntryViewModel
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamUiState
import hu.bme.aut.android.examapp.ui.viewmodel.topic.TopicListViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewExamScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: ExamEntryViewModel = viewModel { ExamEntryViewModel() }//viewModel(factory = AppViewModelProvider.Factory)
) {
    when (viewModel.examEntryScreenUiState) {
        ExamEntryScreenUiState.Loading -> CircularProgressIndicator(modifier = Modifier.fillMaxWidth())
        ExamEntryScreenUiState.Success ->  NewExamScreenUiState(viewModel, navigateBack)
        ExamEntryScreenUiState.Error -> Text(text = ExamEntryScreenUiState.Error.errorMessage.ifBlank { "Unexpected error " })
    }
}

@Composable
private fun NewExamScreenUiState(
    viewModel: ExamEntryViewModel,
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
         topBar = { TopAppBarContent(stringResource(Res.string.exam_create), navigateBack) },
    ) { innerPadding ->
        ExamEntryBody(
            examUiState = viewModel.examUiState,
            onExamValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    if (viewModel.saveExam()) {
                        navigateBack()
                    } else {
                        showNotify = true
                        notifyMessage = "Exam with this name already exists"
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
fun ExamEntryBody(
    examUiState: ExamUiState,
    onExamValueChange: (ExamDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.padding(16.dp)
    ) {
        ExamInputForm(
            examDetails = examUiState.examDetails,
            onValueChange = onExamValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = examUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.save_action))
        }
    }
}

@Composable
fun ExamInputForm(
    examDetails: ExamDetails,
    modifier: Modifier = Modifier,
    onValueChange: (ExamDetails) -> Unit = {},
    enabled: Boolean = true,
    entryViewModel: ExamEntryViewModel = viewModel { ExamEntryViewModel() },//viewModel(factory = AppViewModelProvider.Factory),
    topicListViewModel: TopicListViewModel = viewModel { TopicListViewModel() },//viewModel(factory = AppViewModelProvider.Factory),

    ) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = examDetails.name,
            onValueChange = { onValueChange(examDetails.copy(name = it)) },
            label = { Text(stringResource(Res.string.exam_name_req)) },
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
            name = stringResource(Res.string.exam),
            items = topicListViewModel.topicListUiState.topicList.map{it.topic}.filterNot{ it == examDetails.topicName },
            onChoose = {topic ->
                coroutineScope.launch{
                    onValueChange(examDetails.copy(topicId = entryViewModel.getTopicIdByTopic(topic)))
                }
            },
            default = examDetails.topicName,
            modifier = Modifier.fillMaxWidth(),
        )
        if (enabled) {
            Text(
                text = stringResource(Res.string.required_fields),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}