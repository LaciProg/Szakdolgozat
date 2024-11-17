package hu.bme.aut.android.examapp.ui.topic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import examapp.composeapp.generated.resources.topic_list
//import androidx.hilt.navigation.compose.hiltViewModel
import hu.bme.aut.android.examapp.service.api.dto.*
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.viewmodel.topic.TopicListScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.topic.TopicListViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopicListScreen(
    modifier: Modifier = Modifier,
    addNewTopic: () -> Unit = {},
    navigateToTopicDetails: (String) -> Unit,
    navigateBack: () -> Unit,
    viewModel: TopicListViewModel = viewModel { TopicListViewModel() } ,
  ){
    when(viewModel.topicListScreenUiState){
        is TopicListScreenUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is TopicListScreenUiState.Success -> TopicListResultScreen(
            topics = (viewModel.topicListScreenUiState as TopicListScreenUiState.Success).topics,
            addNewPoint = addNewTopic,
            navigateToTopicDetails = navigateToTopicDetails,
            navigateBack = navigateBack,
        )
        is TopicListScreenUiState.Error -> Text(text = TopicListScreenUiState.Error.errorMessage.ifBlank { "Unexpected error " }, modifier = Modifier.fillMaxSize())
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getAllTopicList()
    }
}

@Composable
fun TopicListResultScreen(
    topics: List<NameDto>,
    addNewPoint: () -> Unit,
    navigateToTopicDetails: (String) -> Unit,
    navigateBack: () -> Unit
){

    Scaffold(
        topBar = { TopAppBarContent(stringResource(Res.string.topic_list), navigateBack) },
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
            if (topics.isEmpty()) {
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
                items(topics) { topic ->
                    TextButton(
                        onClick = { navigateToTopicDetails(topic.uuid) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant) // Sötétebb háttér a gombnak
                            .padding(8.dp)
                    ) {
                        Text(
                            text = topic.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}