package hu.bme.aut.android.examapp.ui.truefalsequestion

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
import hu.bme.aut.android.examapp.api.dto.NameDto
import hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion.TrueFalseQuestionListScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion.TrueFalseQuestionListViewModel

@Composable
fun TrueFalseQuestionListScreen(
    modifier: Modifier = Modifier,
    addNewTrueFalseQuestion: () -> Unit = {},
    navigateToTrueFalseQuestionDetails: (String) -> Unit,
    viewModel: TrueFalseQuestionListViewModel = viewModel { TrueFalseQuestionListViewModel() }//viewModel(factory = AppViewModelProvider.Factory)
  ){
    when(viewModel.trueFalseQuestionListScreenUiState){
        is TrueFalseQuestionListScreenUiState.Loading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        is TrueFalseQuestionListScreenUiState.Success -> TrueFalseQuestionListResultScreen(
            questions =  (viewModel.trueFalseQuestionListScreenUiState as TrueFalseQuestionListScreenUiState.Success).questions,
            addNewQuestion = addNewTrueFalseQuestion,
            navigateToQuestionDetails = navigateToTrueFalseQuestionDetails,
            viewModel = viewModel
        )
        is TrueFalseQuestionListScreenUiState.Error -> Text(text = TrueFalseQuestionListScreenUiState.Error.errorMessage.ifBlank { "Unexpected error " }, modifier = Modifier.fillMaxSize())
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getAllTrueFalseQuestionList()
    }
}

@Composable
fun TrueFalseQuestionListResultScreen(
    questions: List<NameDto>,
    addNewQuestion: () -> Unit,
    navigateToQuestionDetails: (String) -> Unit,
    viewModel: TrueFalseQuestionListViewModel
){
   Scaffold(
        topBar = {  },
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
                TextButton(onClick = { navigateToQuestionDetails(it.uuid) }) {
                    Text(it.name)
                }

            }
        }
    }
}
