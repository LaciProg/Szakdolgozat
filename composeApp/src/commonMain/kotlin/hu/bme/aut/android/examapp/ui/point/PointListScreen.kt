package hu.bme.aut.android.examapp.ui.point

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import examapp.composeapp.generated.resources.Res
import examapp.composeapp.generated.resources.point_list
import hu.bme.aut.android.examapp.service.api.dto.*
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.viewmodel.point.PointListScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.point.PointListViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun PointListScreen(
    modifier: Modifier = Modifier,
    addNewPoint: () -> Unit = {},
    navigateToPointDetails: (String) -> Unit,
    viewModel: PointListViewModel = viewModel { PointListViewModel() },//viewModel(factory = AppViewModelProvider.Factory)
    navigateBack: () -> Unit
){
    when(viewModel.pointListScreenUiState){
        is PointListScreenUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is PointListScreenUiState.Success -> PointListResultScreen(
            points =  (viewModel.pointListScreenUiState as PointListScreenUiState.Success).points,
            addNewPoint = addNewPoint,
            navigateToPointDetails = navigateToPointDetails,
            navigateBack = navigateBack
            )
        is PointListScreenUiState.Error -> Text(text = PointListScreenUiState.Error.errorMessage.ifBlank { "Unexpected error " }, modifier = Modifier.fillMaxSize())
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getAllPointList()
    }

}

@Composable
fun PointListResultScreen(
    points: List<NameDto>,
    addNewPoint: () -> Unit = {},
    navigateToPointDetails: (String) -> Unit,
    navigateBack: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBarContent(stringResource(Res.string.point_list), navigateBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { addNewPoint() },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { padding ->

        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (points.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No true/false questions available",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                items(points) { point ->
                    TextButton(
                        onClick = { navigateToPointDetails(point.uuid) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant) // Sötétebb háttér a gombnak
                            .padding(8.dp)
                    ) {
                        Text(
                            text = point.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}