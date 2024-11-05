package hu.bme.aut.android.examapp.ui.topic

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import examapp.composeapp.generated.resources.Res
import examapp.composeapp.generated.resources.topic_list
//import androidx.hilt.navigation.compose.hiltViewModel
import hu.bme.aut.android.examapp.api.dto.NameDto
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
        is TopicListScreenUiState.Loading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        is TopicListScreenUiState.Success -> TopicListResultScreen(
            topics = (viewModel.topicListScreenUiState as TopicListScreenUiState.Success).topics,
            addNewPoint = addNewTopic,
            navigateToPointDetails = navigateToTopicDetails,
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
    navigateToPointDetails: (String) -> Unit,
    navigateBack: () -> Unit
){

    Scaffold(
        topBar = { TopAppBarContent(stringResource(Res.string.topic_list), navigateBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { addNewPoint() }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { padding ->

        LazyColumn(contentPadding = padding) {
            items(topics){
                TextButton(onClick = { navigateToPointDetails(it.uuid) }) {
                    Text(it.name)
                }
            }
        }
    }
}