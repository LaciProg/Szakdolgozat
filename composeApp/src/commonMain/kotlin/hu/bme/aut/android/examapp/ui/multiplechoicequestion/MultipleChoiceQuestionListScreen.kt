package hu.bme.aut.android.examapp.ui.multiplechoicequestion

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
import examapp.composeapp.generated.resources.multiple_choice_question_list
import hu.bme.aut.android.examapp.api.dto.NameDto
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.viewmodel.multiplechoicequestion.MultipleChoiceQuestionListScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.multiplechoicequestion.MultipleChoiceQuestionListViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun MultipleChoiceQuestionListScreen(
    modifier: Modifier = Modifier,
    addNewMultipleChoiceQuestion: () -> Unit = {},
    navigateToMultipleChoiceQuestionDetails: (String) -> Unit,
    viewModel: MultipleChoiceQuestionListViewModel = viewModel { MultipleChoiceQuestionListViewModel() },//viewModel(factory = AppViewModelProvider.Factory)
    navigateBack: () -> Unit
){
    when(viewModel.multipleChoiceQuestionListScreenUiState){
        is MultipleChoiceQuestionListScreenUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is MultipleChoiceQuestionListScreenUiState.Success -> MultipleChoiceQuestionListResultScreen(
            questions =  (viewModel.multipleChoiceQuestionListScreenUiState as MultipleChoiceQuestionListScreenUiState.Success).questions,
            addNewQuestion = addNewMultipleChoiceQuestion,
            navigateToMultipleChoiceQuestionDetails = navigateToMultipleChoiceQuestionDetails,
            navigateBack = navigateBack
        )
        is MultipleChoiceQuestionListScreenUiState.Error -> Text(text = MultipleChoiceQuestionListScreenUiState.Error.errorMessage.ifBlank { "Unexpected error " }, modifier = Modifier.fillMaxSize())
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getAllMultipleChoiceQuestionList()
    }
}

@Composable
fun MultipleChoiceQuestionListResultScreen(
    questions: List<NameDto>,
    addNewQuestion: () -> Unit,
    navigateToMultipleChoiceQuestionDetails: (String) -> Unit,
    navigateBack: () -> Unit
) {
    Scaffold(
         topBar = { TopAppBarContent(stringResource(Res.string.multiple_choice_question_list), navigateBack) },
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
                        onClick = { navigateToMultipleChoiceQuestionDetails(question.uuid) },
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
