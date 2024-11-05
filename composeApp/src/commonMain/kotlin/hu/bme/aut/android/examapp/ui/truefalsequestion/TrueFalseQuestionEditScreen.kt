package hu.bme.aut.android.examapp.ui.truefalsequestion

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
import examapp.composeapp.generated.resources.true_false_question_create
import examapp.composeapp.generated.resources.true_false_question_edit
import hu.bme.aut.android.examapp.Notify
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion.TrueFalseQuestionEditScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion.TrueFalseQuestionEditViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun TrueFalseQuestionEditScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    trueFalseQuestionId: String,
    viewModel: TrueFalseQuestionEditViewModel = viewModel { TrueFalseQuestionEditViewModel(trueFalseQuestionId) }//viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(trueFalseQuestionId) {
        viewModel.setId(trueFalseQuestionId)
    }
    when (viewModel.trueFalseEditScreenUiState) {
        is TrueFalseQuestionEditScreenUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
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

    Scaffold(
        topBar = { TopAppBarContent(stringResource(Res.string.true_false_question_edit), navigateBack) },
    ){innerPadding ->

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
            modifier = modifier.padding(innerPadding)
        )
    }
}