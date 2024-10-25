package hu.bme.aut.android.examapp.ui.multiplechoicequestion

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
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
        is MultipleChoiceQuestionListScreenUiState.Loading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize())
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
                onClick = { addNewQuestion() }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { padding ->

        LazyColumn(contentPadding = padding) {
            items(questions){
                TextButton(onClick = { navigateToMultipleChoiceQuestionDetails(it.uuid) }) {
                    Text(it.name)
                }

            }
        }
    }
}
