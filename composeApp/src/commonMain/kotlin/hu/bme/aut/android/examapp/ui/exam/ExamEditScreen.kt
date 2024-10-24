package hu.bme.aut.android.examapp.ui.exam

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.bme.aut.android.examapp.Notify
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamEditScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamEditViewModel
import kotlinx.coroutines.launch

@Composable
fun ExamEditScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    savedStateHandle: SavedStateHandle,
    viewModel: ExamEditViewModel = viewModel { ExamEditViewModel(savedStateHandle) }//viewModel(factory = AppViewModelProvider.Factory)
) {
    when(viewModel.examEditScreenUiState){
        is ExamEditScreenUiState.Loading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize())
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
        modifier = modifier
    )
}