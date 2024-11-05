package hu.bme.aut.android.examapp.ui.multiplechoicequestion

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import examapp.composeapp.generated.resources.Res
import examapp.composeapp.generated.resources.*
import hu.bme.aut.android.examapp.api.dto.MultipleChoiceQuestionDto
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.viewmodel.multiplechoicequestion.MultipleChoiceQuestionDetails
import hu.bme.aut.android.examapp.ui.viewmodel.multiplechoicequestion.MultipleChoiceQuestionDetailsScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.multiplechoicequestion.MultipleChoiceQuestionDetailsUiState
import hu.bme.aut.android.examapp.ui.viewmodel.multiplechoicequestion.MultipleChoiceQuestionDetailsViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MultipleChoiceQuestionDetailsScreen(
    navigateToEditMultipleChoiceQuestion: (String) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    multipleChoiceQuestionId: String,
    viewModel: MultipleChoiceQuestionDetailsViewModel = viewModel { MultipleChoiceQuestionDetailsViewModel(multipleChoiceQuestionId) }//viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(multipleChoiceQuestionId) {
        viewModel.setId(multipleChoiceQuestionId)
    }
    when(viewModel.multipleChoiceDetailsScreenUiState){
        is MultipleChoiceQuestionDetailsScreenUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is MultipleChoiceQuestionDetailsScreenUiState.Success -> MultipleChoiceQuestionDetailsScreenUiState(
            question =  (viewModel.multipleChoiceDetailsScreenUiState as MultipleChoiceQuestionDetailsScreenUiState.Success).question,
            navigateToEditTrueFalseQuestion = navigateToEditMultipleChoiceQuestion,
            navigateBack = navigateBack,
            modifier = modifier,
            viewModel = viewModel
        )
        is MultipleChoiceQuestionDetailsScreenUiState.Error -> Text(text = MultipleChoiceQuestionDetailsScreenUiState.Error.errorMessage.ifBlank { "Unexpected error " })
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getQuestion(viewModel.multipleChoiceQuestionId)
    }
}

@Composable
fun MultipleChoiceQuestionDetailsScreenUiState(
    question: MultipleChoiceQuestionDto,
    navigateToEditTrueFalseQuestion: (String) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MultipleChoiceQuestionDetailsViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
         topBar = { TopAppBarContent(stringResource(Res.string.multiple_choice_question_details), navigateBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEditTrueFalseQuestion(viewModel.uiState.multipleChoiceQuestionDetails.id) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(20.dp)

            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(Res.string.edit_question),
                )
            }
        }, modifier = modifier
    ) { innerPadding ->
        MultipleChoiceQuestionDetailsBody(
            multipleChoiceQuestionDetailsUiState = viewModel.uiState,
            onDelete = {
                coroutineScope.launch {
                    viewModel.deleteMultipleChoiceQuestion()
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
fun MultipleChoiceQuestionDetailsBody(
    multipleChoiceQuestionDetailsUiState: MultipleChoiceQuestionDetailsUiState,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
        MultipleChoiceQuestionDetails(
            multipleChoiceQuestion = multipleChoiceQuestionDetailsUiState.multipleChoiceQuestionDetails, modifier = Modifier.fillMaxWidth()
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
fun MultipleChoiceQuestionDetails(
    multipleChoiceQuestion: MultipleChoiceQuestionDetails,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    colors: CardColors = CardDefaults.cardColors(
        containerColor = containerColor,
        contentColor = contentColor
    )
) {
    Card(
        modifier = modifier, colors = colors
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MultipleChoiceQuestionDetailsRow(
                labelResID = Res.string.question,
                multipleChoiceQuestionDetail = multipleChoiceQuestion.question,
                modifier = Modifier.padding(
                    horizontal = 16.dp
                )
            )
            MultipleChoiceQuestionDetailsRow(
                labelResID = Res.string.topic,
                multipleChoiceQuestionDetail = multipleChoiceQuestion.topicName,
                modifier = Modifier.padding(
                    horizontal = 16.dp
                )
            )
            MultipleChoiceQuestionDetailsRow(
                labelResID = Res.string.point,
                multipleChoiceQuestionDetail = multipleChoiceQuestion.pointName,
                modifier = Modifier.padding(
                    horizontal = 16.dp
                )
            )
            //TODO Lehetne szebben is megoldani
            for(correct in multipleChoiceQuestion.correctAnswersList){
                MultipleChoiceQuestionDetailsRow(
                    labelResID = Res.string.correct_answer,
                    multipleChoiceQuestionDetail = correct,
                    modifier = Modifier.padding(
                        horizontal = 16.dp
                    )
                )
            }
        }

    }
}

@Composable
private fun MultipleChoiceQuestionDetailsRow(
    @StringRes labelResID: StringResource, multipleChoiceQuestionDetail: String, modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(text = stringResource(labelResID))
        Spacer(modifier = Modifier.weight(1f))
        Text(text = multipleChoiceQuestionDetail, fontWeight = FontWeight.Bold)
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