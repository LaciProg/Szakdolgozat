package hu.bme.aut.android.examapp.ui.truefalsequestion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import examapp.composeapp.generated.resources.Res
import examapp.composeapp.generated.resources.true_false_question_create
import examapp.composeapp.generated.resources.true_false_question_list
import hu.bme.aut.android.examapp.service.api.dto.*
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion.TrueFalseQuestionListScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion.TrueFalseQuestionListViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun TrueFalseQuestionListScreen(
    modifier: Modifier = Modifier,
    addNewTrueFalseQuestion: () -> Unit = {},
    navigateToTrueFalseQuestionDetails: (String) -> Unit,
    viewModel: TrueFalseQuestionListViewModel = viewModel{ TrueFalseQuestionListViewModel() },
    navigateBack: () -> Unit
) {
    when (viewModel.trueFalseQuestionListScreenUiState) {
        is TrueFalseQuestionListScreenUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is TrueFalseQuestionListScreenUiState.Success -> {
            TrueFalseQuestionListResultScreen(
                questions = (viewModel.trueFalseQuestionListScreenUiState as TrueFalseQuestionListScreenUiState.Success).questions,
                addNewQuestion = addNewTrueFalseQuestion,
                navigateToQuestionDetails = navigateToTrueFalseQuestionDetails,
                navigateBack = navigateBack
            )
        }
        is TrueFalseQuestionListScreenUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = TrueFalseQuestionListScreenUiState.Error.errorMessage.ifBlank { "Unexpected error" },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getAllTrueFalseQuestionList()
    }
}

@Composable
fun TrueFalseQuestionListResultScreen(
    questions: List<NameDto>,
    addNewQuestion: () -> Unit,
    navigateToQuestionDetails: (String) -> Unit,
    navigateBack: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBarContent(stringResource(Res.string.true_false_question_list), navigateBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { addNewQuestion() },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { padding ->

        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (questions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No true/false questions available",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                items(questions) { question ->
                    TextButton(
                        onClick = { navigateToQuestionDetails(question.uuid) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant) // Sötétebb háttér a gombnak
                            .padding(8.dp)
                    ) {
                        Text(
                            text = question.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
