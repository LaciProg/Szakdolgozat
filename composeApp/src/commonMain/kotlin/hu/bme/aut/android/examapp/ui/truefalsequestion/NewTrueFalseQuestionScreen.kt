package hu.bme.aut.android.examapp.ui.truefalsequestion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import examapp.composeapp.generated.resources.Res
import examapp.composeapp.generated.resources.*
import hu.bme.aut.android.examapp.Notify
import hu.bme.aut.android.examapp.ui.components.DropDownList
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.viewmodel.point.PointListViewModel
import hu.bme.aut.android.examapp.ui.viewmodel.topic.TopicListViewModel
import hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion.TrueFalseQuestionDetails
import hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion.TrueFalseQuestionEntryScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion.TrueFalseQuestionEntryViewModel
import hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion.TrueFalseQuestionUiState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewTrueFalseQuestionScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: TrueFalseQuestionEntryViewModel = viewModel {TrueFalseQuestionEntryViewModel() }//viewModel(factory = AppViewModelProvider.Factory)
) {
    when (viewModel.trueFalseEntryScreenUiState) {
        TrueFalseQuestionEntryScreenUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        TrueFalseQuestionEntryScreenUiState.Success -> NewTrueFalseQuestionScreenUiState(viewModel, navigateBack)
        TrueFalseQuestionEntryScreenUiState.Error -> Text(text = TrueFalseQuestionEntryScreenUiState.Error.errorMessage.ifBlank { "Unexpected error " })
    }
}

@Composable
private fun NewTrueFalseQuestionScreenUiState(
    viewModel: TrueFalseQuestionEntryViewModel,
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
         topBar = { TopAppBarContent(stringResource(Res.string.true_false_question_create), navigateBack) },
    ) { innerPadding ->
        TrueFalseQuestionEntryBody(
            trueFalseQuestionUiState = viewModel.trueFalseQuestionUiState,
            onTrueFalseQuestionValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    if (viewModel.saveTrueFalseQuestion()) {
                        navigateBack()
                    } else {
                        showNotify = true
                        notifyMessage = "Question already exists"
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
fun TrueFalseQuestionEntryBody(
    trueFalseQuestionUiState: TrueFalseQuestionUiState,
    onTrueFalseQuestionValueChange: (TrueFalseQuestionDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.padding(16.dp)
    ) {
        TrueFalseQuestionInputForm(
            trueFalseQuestionDetails = trueFalseQuestionUiState.trueFalseQuestionDetails,
            onValueChange = onTrueFalseQuestionValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = trueFalseQuestionUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.save_action))
        }
    }
}

@Composable
fun TrueFalseQuestionInputForm(
    trueFalseQuestionDetails: TrueFalseQuestionDetails,
    modifier: Modifier = Modifier,
    onValueChange: (TrueFalseQuestionDetails) -> Unit = {},
    enabled: Boolean = true,
    topicListViewModel: TopicListViewModel = viewModel { TopicListViewModel() },//viewModel(factory = AppViewModelProvider.Factory),
    pointListViewModel: PointListViewModel = viewModel { PointListViewModel() }//viewModel(factory = AppViewModelProvider.Factory)
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DropDownList(
            name = stringResource(Res.string.topic_name_req),
            items = topicListViewModel.topicListUiState.topicList.map{it.topic}.filterNot{ it == trueFalseQuestionDetails.topicName },
            onChoose = { topicName -> onValueChange(trueFalseQuestionDetails.copy(topicName = topicName, topic = if(topicName.isNotBlank()) topicListViewModel.topicListUiState.topicList.filter { it.topic == topicName }.map{it.id}.first() else "")) },
            default = trueFalseQuestionDetails.topicName,
            modifier = Modifier.fillMaxWidth(),
        )
        DropDownList(
            name = stringResource(Res.string.point_req),
            items = pointListViewModel.pointListUiState.pointList.map{it.point}.filterNot{ it == trueFalseQuestionDetails.pointName },
            onChoose = { pointName -> onValueChange(trueFalseQuestionDetails.copy(pointName = pointName, point = if(pointName.isNotBlank()) pointListViewModel.pointListUiState.pointList.filter { it.point ==  pointName}.map{it.id}.first() else "")) },
            default = trueFalseQuestionDetails.pointName,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = trueFalseQuestionDetails.question,
            onValueChange = { onValueChange(trueFalseQuestionDetails.copy(question = it)) },
            label = { Text(stringResource(Res.string.question_name_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        Column {
            Text(stringResource(Res.string.correct_answer_req))
            TextButton( onClick = { onValueChange(trueFalseQuestionDetails.copy(correctAnswer = true, isAnswerChosen = true)) })
            {
                Text(
                    text = stringResource(Res.string.true_action),
                    color = if(!trueFalseQuestionDetails.isAnswerChosen) MaterialTheme.colorScheme.onSurface
                        else if (trueFalseQuestionDetails.correctAnswer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
            TextButton(onClick = { onValueChange(trueFalseQuestionDetails.copy(correctAnswer = false,  isAnswerChosen = true)) }) {
                Text(
                    text = stringResource(Res.string.false_action),
                    color = if(!trueFalseQuestionDetails.isAnswerChosen) MaterialTheme.colorScheme.onSurface
                        else if (!trueFalseQuestionDetails.correctAnswer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
        if (enabled) {
            Text(
                text = stringResource(Res.string.required_fields),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}