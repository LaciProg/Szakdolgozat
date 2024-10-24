package hu.bme.aut.android.examapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import examapp.composeapp.generated.resources.Res
import hu.bme.aut.android.examapp.api.dto.TopicDto
import hu.bme.aut.android.examapp.navigation.ExamNavHost
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.StringResource
import examapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

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
fun MainScreen(
    navigateToTopicList: () -> Unit,
    navigateToPointList: () -> Unit,
    navigateToTrueFalseQuestionList: () -> Unit,
    navigateToMultipleChoiceQuestionList: () -> Unit,
    navigateToExamList: () -> Unit,
    navigateToExportExamList: () -> Unit,
    navigateToSubmission: () -> Unit,
    onSignOut: () -> Unit,
    //viewModel: MainScreenViewModel = hiltViewModel()//viewModel(factory = AppViewModelProvider.Factory),
){
    /*val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        try{
            viewModel.authenticate()
        } catch (e: Exception){
            Toast.makeText(
                context,
                "Authentication failed",
                Toast.LENGTH_SHORT
            ).show()
        }
    }*/
    Scaffold(
        /*floatingActionButton = {
            FloatingActionButton(
                onClick = {viewModel.signOut(); onSignOut() },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(20.dp)

            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = stringResource(Res.string.log_out),
                )
            }
        }*/
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.wrapContentSize(),
                text = "Welcome to the Exam App!"
            )
            OutlinedButton(
                onClick = { navigateToTopicList() },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.topic))
            }
            OutlinedButton(
                onClick = { navigateToPointList() },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.point))
            }
            OutlinedButton(
                onClick = { navigateToTrueFalseQuestionList() },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.true_false_question))
            }
            OutlinedButton(
                onClick = { navigateToMultipleChoiceQuestionList() },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.multiple_choice_question))
            }
            OutlinedButton(
                onClick = { navigateToExamList() },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.exams))
            }
            OutlinedButton(
                onClick = { navigateToSubmission() },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.submission))
            }
            /*OutlinedButton(
                onClick = { navigateToExportExamList() },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.export_exams))
            }*/
        }

    }

}

@Composable
internal expect fun Notify(message: String)
