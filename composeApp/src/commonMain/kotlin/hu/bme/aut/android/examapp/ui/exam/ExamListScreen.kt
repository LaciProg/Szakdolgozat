package hu.bme.aut.android.examapp.ui.exam

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
import examapp.composeapp.generated.resources.exam_list
import hu.bme.aut.android.examapp.api.dto.NameDto
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamListScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamListViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExamListScreen(
    modifier: Modifier = Modifier,
    addNewExam: () -> Unit = {},
    navigateToExamDetails: (String) -> Unit,
    viewModel: ExamListViewModel = viewModel { ExamListViewModel() },//viewModel(factory = AppViewModelProvider.Factory)
    navigateBack: () -> Unit
){
    when(viewModel.examListScreenUiState){
        is ExamListScreenUiState.Loading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        is ExamListScreenUiState.Success -> ExamListResultScreen(
            exams =  (viewModel.examListScreenUiState as ExamListScreenUiState.Success).exams,
            addNewExam = addNewExam,
            navigateToExamDetails = navigateToExamDetails,
            navigateBack= navigateBack
        )
        is ExamListScreenUiState.Error -> Text(text = ExamListScreenUiState.Error.errorMessage.ifBlank { "Unexpected error " }, modifier = Modifier.fillMaxSize())
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getAllExamList()
    }
}

@Composable
private fun ExamListResultScreen(
    exams: List<NameDto>,
    addNewExam: () -> Unit,
    navigateToExamDetails: (String) -> Unit,
    navigateBack: () -> Unit
) {
    Scaffold(
         topBar = { TopAppBarContent(stringResource(Res.string.exam_list), navigateBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { addNewExam() }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { padding ->

        LazyColumn(contentPadding = padding) {
            items(exams){
                TextButton(onClick = { navigateToExamDetails(it.uuid) }) {
                    Text(it.name)
                }

            }
        }
    }
}