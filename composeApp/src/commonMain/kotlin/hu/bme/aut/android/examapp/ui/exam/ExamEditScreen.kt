package hu.bme.aut.android.examapp.ui.exam

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import examapp.composeapp.generated.resources.Res
import examapp.composeapp.generated.resources.exam_edit
import hu.bme.aut.android.examapp.Notify
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamEditScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamEditViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExamEditScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    examId: String,
    viewModel: ExamEditViewModel = viewModel { ExamEditViewModel(examId) }//viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(examId) {
        viewModel.setId(examId)
    }
    when(viewModel.examEditScreenUiState){
        is ExamEditScreenUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is ExamEditScreenUiState.Success -> ExamEditResultScreen(
            navigateBack = navigateBack,
            viewModel = viewModel,
            modifier = modifier
        )
        is ExamEditScreenUiState.Error -> Text(text = ExamEditScreenUiState.Error.errorMessage.ifBlank { "Unexpected error " })
    }
}

@Composable
fun ExamEditResultScreen(
    navigateBack: () -> Unit,
    viewModel: ExamEditViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var showNotify by remember { mutableStateOf(false) }
    var notifyMessage by remember { mutableStateOf("") }

    if (showNotify) {
        Notify(notifyMessage)
        showNotify = false
    }
    Scaffold(
        topBar = { TopAppBarContent(stringResource(Res.string.exam_edit), navigateBack) },
    ){innerPadding ->
        ExamEntryBody(
            examUiState = viewModel.examUiState,
            onExamValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    if(viewModel.updateExam()){
                        navigateBack()
                    }
                    else{
                        showNotify = true
                        notifyMessage =  "Exam with this name already exists"
                    }
                }
            },
            modifier = modifier.padding(innerPadding)
        )
    }
}