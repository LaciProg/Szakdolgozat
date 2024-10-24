package hu.bme.aut.android.examapp.ui.point

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import examapp.composeapp.generated.resources.Res
import examapp.composeapp.generated.resources.*
import hu.bme.aut.android.examapp.api.dto.PointDto
import hu.bme.aut.android.examapp.ui.viewmodel.point.PointDetailsScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.point.PointDetailsUiState
import hu.bme.aut.android.examapp.ui.viewmodel.point.PointDetailsViewModel
import hu.bme.aut.android.examapp.ui.viewmodel.point.toPoint
import hu.bme.aut.android.examapp.ui.viewmodel.point.toPointDetails
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PointDetailsScreen(
    navigateToEditPoint: (String) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    savedStateHandle: SavedStateHandle,
    viewModel: PointDetailsViewModel = viewModel { PointDetailsViewModel(savedStateHandle) }//viewModel(factory = AppViewModelProvider.Factory)
) {
    when(viewModel.pointDetailsScreenUiState){
        is PointDetailsScreenUiState.Loading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        is PointDetailsScreenUiState.Success -> PointDetailsResultScreen(
            point =  (viewModel.pointDetailsScreenUiState as PointDetailsScreenUiState.Success).point,
            navigateToEditPoint = navigateToEditPoint,
            navigateBack = navigateBack,
            modifier = modifier,
            viewModel = viewModel
        )
        is PointDetailsScreenUiState.Error -> Text(text = PointDetailsScreenUiState.Error.errorMessage.ifBlank { "Unexpected error " })
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getPoint(viewModel.pointId)
    }
}

@Composable
fun PointDetailsResultScreen(
    point: PointDto,
    navigateToEditPoint: (String) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PointDetailsViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {},
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEditPoint(point.uuid) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(20.dp)

            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(Res.string.edit_point),
                )
            }
        }, modifier = modifier
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)) {

            PointDetailsBody(
                pointDetailsUiState = PointDetailsUiState(point.toPointDetails()),
                onDelete = {
                    coroutineScope.launch {
                        viewModel.deletePoint()
                        navigateBack()
                    }
                },
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            )
        }

    }
}

@Composable
private fun PointDetailsBody(
    pointDetailsUiState: PointDetailsUiState,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
        PointDetails(
            point = pointDetailsUiState.pointDetails.toPoint(), modifier = Modifier.fillMaxWidth()
        )
        OutlinedButton(
            onClick = { deleteConfirmationRequired = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.delete))
        }
        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onDelete()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}


@Composable
fun PointDetails(
    point: PointDto, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PointDetailsRow(
                labelResID = Res.string.point_type,
                pointDetail = point.type,
                modifier = Modifier.padding(
                    horizontal = 16.dp
                )
            )
            PointDetailsRow(
                labelResID = Res.string.point,
                pointDetail = point.point.toString(),
                modifier = Modifier.padding(
                    horizontal = 16.dp
                )
            )
            PointDetailsRow(
                labelResID = Res.string.good_answer,
                pointDetail = point.goodAnswer.toString(),
                modifier = Modifier.padding(
                    horizontal = 16.dp
                )
            )
            PointDetailsRow(
                labelResID = Res.string.bad_answer,
                pointDetail = point.badAnswer.toString(),
                modifier = Modifier.padding(
                    horizontal = 16.dp
                )
            )
        }

    }
}

@Composable
private fun PointDetailsRow(
    @StringRes labelResID: StringResource, pointDetail: String, modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(text = stringResource(labelResID))
        Spacer(modifier = Modifier.weight(1f))
        Text(text = pointDetail, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit, onDeleteCancel: () -> Unit, modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(Res.string.attention)) },
        text = { Text(stringResource(Res.string.delete_question)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = stringResource(Res.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = stringResource(Res.string.yes))
            }
        })
}
