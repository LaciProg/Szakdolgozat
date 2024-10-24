package hu.bme.aut.android.examapp.ui.multiplechoicequestion

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.bme.aut.android.examapp.Notify
import hu.bme.aut.android.examapp.ui.viewmodel.multiplechoicequestion.MultipleChoiceQuestionEditScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.multiplechoicequestion.MultipleChoiceQuestionEditViewModel
import kotlinx.coroutines.launch

@Composable
fun MultipleChoiceQuestionEditScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    savedStateHandle: SavedStateHandle,
    viewModel: MultipleChoiceQuestionEditViewModel = viewModel { MultipleChoiceQuestionEditViewModel(savedStateHandle) }//viewModel(factory = AppViewModelProvider.Factory)
) {
    when (viewModel.multipleChoiceEditScreenUiState) {
        is MultipleChoiceQuestionEditScreenUiState.Loading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        is MultipleChoiceQuestionEditScreenUiState.Success -> MultipleChoiceQuestionEditResultScreen(
            navigateBack = navigateBack,
            viewModel = viewModel,
            modifier = modifier
        )
        is MultipleChoiceQuestionEditScreenUiState.Error -> Text(text = MultipleChoiceQuestionEditScreenUiState.Error.errorMessage.ifBlank { "Unexpected error " })
    }
}

@Composable
fun MultipleChoiceQuestionEditResultScreen(
    navigateBack: () -> Unit,
    viewModel: MultipleChoiceQuestionEditViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var showNotify by remember { mutableStateOf(false) }
    var notifyMessage by remember { mutableStateOf("") }

    if (showNotify) {
        Notify(notifyMessage)
        showNotify = false
    }

    MultipleChoiceQuestionEntryBody(
        multipleChoiceQuestionUiState = viewModel.multipleChoiceQuestionUiState,
        onMultipleChoiceQuestionValueChange = viewModel::updateUiState,
        onSaveClick = {
            coroutineScope.launch {
                if(viewModel.updateMultipleChoiceQuestion()){
                    navigateBack()
                }
                else{
                    showNotify = true
                    notifyMessage = "Question with this name already exists"
                }
            }
        },
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
    )
}