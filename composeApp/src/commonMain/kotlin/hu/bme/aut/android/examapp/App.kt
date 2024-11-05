package hu.bme.aut.android.examapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import examapp.composeapp.generated.resources.Res
import hu.bme.aut.android.examapp.api.dto.TopicDto
import hu.bme.aut.android.examapp.navigation.ExamNavHost
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import examapp.composeapp.generated.resources.*

@Composable
@Preview
fun App() {
    val scope = rememberCoroutineScope()  // Coroutine scope to launch async network requests
    var showContent by remember { mutableStateOf(false) }
    var topicData by remember { mutableStateOf<List<TopicDto>?>(null) }  // Store the topic data

    MaterialTheme {

        NavigationComponent()
        /*
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                showContent = !showContent
                if (showContent) {
                    scope.launch {
                        topicData = ApiService.getAllTopics()  // Fetch topics from API
                    }
                }
            }) {
                Text("Click me!")
            }

            AnimatedVisibility(showContent) {
                topicData?.let { topics ->
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        topics.forEach { topic ->
                            Text(text = "Topic: ${topic.topic}")
                        }
                    }
                } ?: Text("Loading topics...")
            }
        }*/
    }
}

@Composable
fun NavigationComponent() {
    MaterialTheme {
        val navController = rememberNavController()

        Scaffold() { innerPadding ->
            ExamNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }

    }
}


@Composable
internal expect fun Notify(message: String)
