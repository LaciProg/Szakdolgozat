package hu.bme.aut.android.examapp.ui.exam

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
import examapp.composeapp.generated.resources.exam_list
import hu.bme.aut.android.examapp.service.api.dto.*
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
        is ExamListScreenUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
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
                onClick = { addNewExam() },
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
            if (exams.isEmpty()) {
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
                items(exams) { exam ->
                    TextButton(
                        onClick = { navigateToExamDetails(exam.uuid) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant) // Sötétebb háttér a gombnak
                            .padding(8.dp)
                    ) {
                        Text(
                            text = exam.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}