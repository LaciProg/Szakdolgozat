package hu.bme.aut.android.examapp.ui.multiplechoicequestion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import examapp.composeapp.generated.resources.multiple_choice_question_edit
import hu.bme.aut.android.examapp.Notify
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.viewmodel.multiplechoicequestion.MultipleChoiceQuestionEditScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.multiplechoicequestion.MultipleChoiceQuestionEditViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun MultipleChoiceQuestionEditScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    multipleChoiceQuestionId: String,
    viewModel: MultipleChoiceQuestionEditViewModel = viewModel { MultipleChoiceQuestionEditViewModel(multipleChoiceQuestionId) }//viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(multipleChoiceQuestionId) {
        viewModel.setId(multipleChoiceQuestionId)
    }
    when (viewModel.multipleChoiceEditScreenUiState) {
        is MultipleChoiceQuestionEditScreenUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
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

    Scaffold(
        topBar = { TopAppBarContent(stringResource(Res.string.multiple_choice_question_edit), navigateBack) },
    ){innerPadding ->

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
                .fillMaxWidth().padding(innerPadding)
        )
    }
}