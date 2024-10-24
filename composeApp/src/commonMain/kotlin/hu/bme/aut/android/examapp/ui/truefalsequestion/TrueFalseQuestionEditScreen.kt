package hu.bme.aut.android.examapp.ui.truefalsequestion

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.bme.aut.android.examapp.Notify
import hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion.TrueFalseQuestionEditScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion.TrueFalseQuestionEditViewModel
import kotlinx.coroutines.launch

@Composable
fun TrueFalseQuestionEditScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    savedStateHandle: SavedStateHandle,
    viewModel: TrueFalseQuestionEditViewModel = viewModel { TrueFalseQuestionEditViewModel(savedStateHandle) }//viewModel(factory = AppViewModelProvider.Factory)
) {
    when (viewModel.trueFalseEditScreenUiState) {
        is TrueFalseQuestionEditScreenUiState.Loading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        is TrueFalseQuestionEditScreenUiState.Success -> TrueFalseQuestionEditResultScreen(
            navigateBack = navigateBack,
            viewModel = viewModel,
            modifier = modifier
        )
        is TrueFalseQuestionEditScreenUiState.Error -> Text(text = TrueFalseQuestionEditScreenUiState.Error.errorMessage.ifBlank { "Unexpected error " })
    }
}

@Composable
fun TrueFalseQuestionEditResultScreen(
    navigateBack: () -> Unit,
    viewModel: TrueFalseQuestionEditViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var showNotify by remember { mutableStateOf(false) }
    var notifyMessage by remember { mutableStateOf("") }

    if (showNotify) {
        Notify(notifyMessage)
        showNotify = false
    }

    TrueFalseQuestionEntryBody(
        trueFalseQuestionUiState = viewModel.trueFalseQuestionUiState,
        onTrueFalseQuestionValueChange = viewModel::updateUiState,
        onSaveClick = {
            coroutineScope.launch {
                if(viewModel.updateTrueFalseQuestion()){
                    navigateBack()
                }
                else{
                    showNotify = true
                    notifyMessage = "Question with this name already exists"
                }
            }
        },
        modifier = modifier
    )
}