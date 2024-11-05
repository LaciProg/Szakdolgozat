package hu.bme.aut.android.examapp.ui.point

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import examapp.composeapp.generated.resources.Res
import examapp.composeapp.generated.resources.point_edit
import hu.bme.aut.android.examapp.Notify
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.viewmodel.point.PointEditScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.point.PointEditViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun PointEditScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    pointId: String,
    viewModel: PointEditViewModel = viewModel { PointEditViewModel(pointId) }
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

    Scaffold(
        topBar = { TopAppBarContent(stringResource(Res.string.point_edit), navigateBack) },
    ){innderPadding ->

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
            modifier = modifier.padding(innderPadding)
        )
    }
}
