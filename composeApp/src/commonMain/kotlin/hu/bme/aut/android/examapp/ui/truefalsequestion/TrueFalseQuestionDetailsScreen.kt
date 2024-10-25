package hu.bme.aut.android.examapp.ui.truefalsequestion

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import examapp.composeapp.generated.resources.Res
import examapp.composeapp.generated.resources.*
import hu.bme.aut.android.examapp.api.dto.TrueFalseQuestionDto
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion.TrueFalseQuestionDetails
import hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion.TrueFalseQuestionDetailsScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion.TrueFalseQuestionDetailsUiState
import hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion.TrueFalseQuestionDetailsViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TrueFalseQuestionDetailsScreen(
    navigateToEditTrueFalseQuestion: (String) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    savedStateHandle: SavedStateHandle,
    viewModel: TrueFalseQuestionDetailsViewModel = viewModel { TrueFalseQuestionDetailsViewModel(savedStateHandle) }//viewModel(factory = AppViewModelProvider.Factory)
) {
    when(viewModel.trueFalseDetailsScreenUiState){
        is TrueFalseQuestionDetailsScreenUiState.Loading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        is TrueFalseQuestionDetailsScreenUiState.Success -> TrueFalseQuestionDetailsScreenUiState(
            question =  (viewModel.trueFalseDetailsScreenUiState as TrueFalseQuestionDetailsScreenUiState.Success).question,
            navigateToEditTrueFalseQuestion = navigateToEditTrueFalseQuestion,
            navigateBack = navigateBack,
            modifier = modifier,
            viewModel = viewModel
        )
        is TrueFalseQuestionDetailsScreenUiState.Error -> Text(text = TrueFalseQuestionDetailsScreenUiState.Error.errorMessage.ifBlank { "Unexpected error " })
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getQuestion(viewModel.trueFalseQuestionId)
    }
}

@Composable
fun TrueFalseQuestionDetailsScreenUiState(
    question: TrueFalseQuestionDto,
    navigateToEditTrueFalseQuestion: (String) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrueFalseQuestionDetailsViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
       topBar = { TopAppBarContent(stringResource(Res.string.true_false_question_details), navigateBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEditTrueFalseQuestion(viewModel.uiState.trueFalseQuestionDetails.id) },
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
        TrueFalseQuestionDetailsBody(
            trueFalseQuestionDetailsUiState = viewModel.uiState,
            onDelete = {
                coroutineScope.launch {
                    viewModel.deleteTrueFalseQuestion()
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
private fun TrueFalseQuestionDetailsBody(
    trueFalseQuestionDetailsUiState: TrueFalseQuestionDetailsUiState,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
        TrueFalseQuestionDetails(
            trueFalseQuestion = trueFalseQuestionDetailsUiState.trueFalseQuestionDetails, modifier = Modifier.fillMaxWidth()
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
fun TrueFalseQuestionDetails(
    trueFalseQuestion: TrueFalseQuestionDetails,
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
            TrueFalseQuestionDetailsRow(
                labelResID = Res.string.question,
                trueFalseQuestionDetail = trueFalseQuestion.question,
                modifier = Modifier.padding(
                    horizontal =16.dp
                )
            )
            TrueFalseQuestionDetailsRow(
                labelResID = Res.string.topic,
                trueFalseQuestionDetail = trueFalseQuestion.topicName,
                modifier = Modifier.padding(
                    horizontal =16.dp
                )
            )
            TrueFalseQuestionDetailsRow(
                labelResID = Res.string.point,
                trueFalseQuestionDetail = trueFalseQuestion.pointName,
                modifier = Modifier.padding(
                    horizontal =16.dp
                )
            )
            TrueFalseQuestionDetailsRow(
                labelResID = Res.string.correct_answer,
                trueFalseQuestionDetail = trueFalseQuestion.correctAnswer.toString(),
                modifier = Modifier.padding(
                    horizontal =16.dp
                )
            )
        }

    }
}

@Composable
private fun TrueFalseQuestionDetailsRow(
    @StringRes labelResID: StringResource, trueFalseQuestionDetail: String, modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(text = stringResource(labelResID))
        Spacer(modifier = Modifier.weight(1f))
        Text(text = trueFalseQuestionDetail, fontWeight = FontWeight.Bold)
    }
}

//TODO: Move to a separate file
@Composable
fun DeleteConfirmationDialog(
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
