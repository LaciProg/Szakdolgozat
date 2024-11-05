package hu.bme.aut.android.examapp.ui.point

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import examapp.composeapp.generated.resources.Res
import examapp.composeapp.generated.resources.*
import hu.bme.aut.android.examapp.Notify
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.viewmodel.point.PointDetails
import hu.bme.aut.android.examapp.ui.viewmodel.point.PointEntryScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.point.PointEntryViewModel
import hu.bme.aut.android.examapp.ui.viewmodel.point.PointUiState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewPoint(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: PointEntryViewModel = viewModel { PointEntryViewModel() },
    //viewModel: PointEntryViewModel = hiltViewModel()//viewModel(factory = AppViewModelProvider.Factory)
) {
    when(viewModel.pointScreenUiState){
        PointEntryScreenUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        PointEntryScreenUiState.Success -> NewPointScreenUiState(viewModel, navigateBack)
        PointEntryScreenUiState.Error -> Text(text = PointEntryScreenUiState.Error.errorMessage.ifBlank { "Unexpected error " })
    }
}

@Composable
private fun NewPointScreenUiState(
    viewModel: PointEntryViewModel,
    navigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var showNotify by remember { mutableStateOf(false) }
    var notifyMessage by remember { mutableStateOf("") }

    if (showNotify) {
        Notify(notifyMessage)
    }

    Scaffold(
         topBar = { TopAppBarContent(stringResource(Res.string.point_create), navigateBack) },
    ) { innerPadding ->
        PointEntryBody(
            pointUiState = viewModel.pointUiState,
            onPointValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    if (viewModel.savePoint()) {
                        navigateBack()
                    } else {
                        showNotify = true
                        notifyMessage = "Point with this name already exists"
                    }
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun PointEntryBody(
    pointUiState: PointUiState,
    onPointValueChange: (PointDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    onPointValueChange(pointUiState.pointDetails)
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.padding(16.dp)
    ) {
        PointInputForm(
            pointDetails = pointUiState.pointDetails,
            onValueChange = onPointValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = pointUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.save_action))
        }
    }
}

@Composable
fun PointInputForm(
    pointDetails: PointDetails,
    modifier: Modifier = Modifier,
    onValueChange: (PointDetails) -> Unit = {},
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = pointDetails.type,
            onValueChange = { onValueChange(pointDetails.copy(type = it)) },
            label = { Text(stringResource(Res.string.point_type_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = pointDetails.point,
            onValueChange = { onValueChange(pointDetails.copy(point = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            label = { Text(stringResource(Res.string.point_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = pointDetails.goodAnswer,
            onValueChange = { onValueChange(pointDetails.copy(goodAnswer = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            label = { Text(stringResource(Res.string.good_answer_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = pointDetails.badAnswer,
            onValueChange = { onValueChange(pointDetails.copy(badAnswer = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(Res.string.bad_answer_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        if (enabled) {
            Text(
                text = stringResource(Res.string.required_fields),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}