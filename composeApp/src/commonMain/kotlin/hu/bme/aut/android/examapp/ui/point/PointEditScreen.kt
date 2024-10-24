package hu.bme.aut.android.examapp.ui.point

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.bme.aut.android.examapp.Notify
import hu.bme.aut.android.examapp.ui.viewmodel.point.PointEditScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.point.PointEditViewModel
import kotlinx.coroutines.launch

@Composable
fun PointEditScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    savedStateHandle: SavedStateHandle,
    viewModel: PointEditViewModel = viewModel { PointEditViewModel(savedStateHandle) }//viewModel(factory = AppViewModelProvider.Factory)
) {
    when (viewModel.pointEditScreenUiState) {
        is PointEditScreenUiState.Loading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        is PointEditScreenUiState.Success -> PointEditResultScreen(
            navigateBack = navigateBack,
            viewModel = viewModel,
            modifier = modifier
        )
        is PointEditScreenUiState.Error -> Text(text = PointEditScreenUiState.Error.errorMessage.ifBlank { "Unexpected error " })
    }

}

@Composable
fun PointEditResultScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PointEditViewModel
){
    val coroutineScope = rememberCoroutineScope()
    var showNotify by remember { mutableStateOf(false) }
    var notifyMessage by remember { mutableStateOf("") }

    if (showNotify) {
        Notify(notifyMessage)
    }
    PointEntryBody(
        pointUiState = viewModel.pointUiState,
        onPointValueChange = viewModel::updateUiState,
        onSaveClick = {
            coroutineScope.launch {
                if(viewModel.updatePoint()) { navigateBack() }
                else{
                    showNotify = true
                    notifyMessage = "Point with this name already exists"
                }
            }
        },
        modifier = modifier
    )
}
