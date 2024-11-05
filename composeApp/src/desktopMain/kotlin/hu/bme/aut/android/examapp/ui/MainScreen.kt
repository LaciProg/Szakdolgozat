package hu.bme.aut.android.examapp.ui

import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hu.bme.aut.android.examapp.pdf.ExportExamDetailsScreen
import hu.bme.aut.android.examapp.ui.exam.ExamDetailsScreen
import hu.bme.aut.android.examapp.ui.exam.ExamEditScreen
import hu.bme.aut.android.examapp.ui.exam.ExamListScreen
import hu.bme.aut.android.examapp.ui.exam.NewExamScreen
import hu.bme.aut.android.examapp.ui.multiplechoicequestion.MultipleChoiceQuestionDetailsScreen
import hu.bme.aut.android.examapp.ui.multiplechoicequestion.MultipleChoiceQuestionEditScreen
import hu.bme.aut.android.examapp.ui.multiplechoicequestion.MultipleChoiceQuestionListScreen
import hu.bme.aut.android.examapp.ui.multiplechoicequestion.NewMultipleChoiceQuestionScreen
import hu.bme.aut.android.examapp.ui.point.NewPoint
import hu.bme.aut.android.examapp.ui.point.PointDetailsScreen
import hu.bme.aut.android.examapp.ui.point.PointEditScreen
import hu.bme.aut.android.examapp.ui.point.PointListScreen
import hu.bme.aut.android.examapp.ui.submission.SubmissionScreen
import hu.bme.aut.android.examapp.ui.topic.NewTopic
import hu.bme.aut.android.examapp.ui.topic.TopicDetailsScreen
import hu.bme.aut.android.examapp.ui.topic.TopicEditScreen
import hu.bme.aut.android.examapp.ui.topic.TopicListScreen
import hu.bme.aut.android.examapp.ui.truefalsequestion.NewTrueFalseQuestionScreen
import hu.bme.aut.android.examapp.ui.truefalsequestion.TrueFalseQuestionDetailsScreen
import hu.bme.aut.android.examapp.ui.truefalsequestion.TrueFalseQuestionEditScreen
import hu.bme.aut.android.examapp.ui.truefalsequestion.TrueFalseQuestionListScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
actual fun MainScreen(
    navigateToTopicList: () -> Unit,
    navigateToPointList: () -> Unit,
    navigateToTrueFalseQuestionList: () -> Unit,
    navigateToMultipleChoiceQuestionList: () -> Unit,
    navigateToExamList: () -> Unit,
    navigateToExportExamList: () -> Unit,
    navigateToSubmission: () -> Unit,
    onSignOut: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    var selectedContent by remember { mutableStateOf(MainContent.WELCOME) }
    var topicId by remember { mutableStateOf("")}
    var pointId by remember { mutableStateOf("")}
    var tfId by remember { mutableStateOf("")}
    var multiId by remember { mutableStateOf("")}
    var examId by remember { mutableStateOf("")}
    var submissionId by remember { mutableStateOf("")}
    var exportId by remember { mutableStateOf("")}
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Exam App") },
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { scaffoldState.drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        drawerContent = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Menu", style = MaterialTheme.typography.h6, modifier = Modifier.padding(bottom = 8.dp))
                Divider()

                // Drawer items with state change
                DrawerButton("Topics", { selectedContent = MainContent.TOPICS }, scaffoldState, coroutineScope)
                DrawerButton("Points", { selectedContent = MainContent.POINTS }, scaffoldState, coroutineScope)
                DrawerButton("True/False Questions", { selectedContent = MainContent.TRUE_FALSE_QUESTIONS }, scaffoldState, coroutineScope)
                DrawerButton("Multiple Choice Questions", { selectedContent = MainContent.MULTIPLE_CHOICE_QUESTIONS }, scaffoldState, coroutineScope)
                DrawerButton("Exams", { selectedContent = MainContent.EXAMS }, scaffoldState, coroutineScope)
                DrawerButton("Submission", { selectedContent = MainContent.SUBMISSIONS }, scaffoldState, coroutineScope)
                DrawerButton("Export Exams", { selectedContent = MainContent.EXPORT_EXAMS }, scaffoldState, coroutineScope)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            when (selectedContent) {
                MainContent.WELCOME -> Text("Welcome to the Exam App!", modifier = Modifier.wrapContentSize())
                MainContent.TOPICS -> TopicListScreen(
                    addNewTopic = { selectedContent = MainContent.TOPIC_NEW },
                    navigateToTopicDetails = { topicId = it; selectedContent = MainContent.TOPIC_DETAILS },
                    navigateBack = { selectedContent = MainContent.WELCOME }
                )
                MainContent.TOPIC_DETAILS -> TopicDetailsScreen(
                    navigateToEditTopic = { selectedContent = MainContent.TOPIC_EDIT },
                    navigateBack = { selectedContent = MainContent.TOPICS },
                    topicId = topicId,
                )
                MainContent.TOPIC_NEW -> NewTopic(
                    navigateBack = { selectedContent = MainContent.TOPICS },
                    onNavigateUp = { selectedContent = MainContent.TOPICS },
                )
                MainContent.TOPIC_EDIT -> TopicEditScreen(
                    navigateBack = { selectedContent = MainContent.TOPIC_DETAILS },
                    topicId = topicId,
                )

                MainContent.POINTS -> PointListScreen(
                    addNewPoint = { selectedContent = MainContent.POINT_NEW },
                    navigateToPointDetails = { pointId = it; selectedContent = MainContent.POINT_DETAILS },
                    navigateBack = { selectedContent = MainContent.WELCOME }
                )
                MainContent.POINT_DETAILS -> PointDetailsScreen(
                    navigateToEditPoint = { selectedContent = MainContent.POINT_EDIT },
                    navigateBack = { selectedContent = MainContent.POINTS },
                    pointId = pointId,
                )
                MainContent.POINT_NEW -> NewPoint(
                    navigateBack = { selectedContent = MainContent.POINTS },
                    onNavigateUp = { selectedContent = MainContent.POINTS },
                )
                MainContent.POINT_EDIT -> PointEditScreen(
                    navigateBack = { selectedContent = MainContent.POINT_DETAILS },
                    pointId = pointId,
                )

                MainContent.TRUE_FALSE_QUESTIONS -> TrueFalseQuestionListScreen(
                    addNewTrueFalseQuestion = { selectedContent = MainContent.TRUE_FALSE_QUESTION_NEW },
                    navigateToTrueFalseQuestionDetails = { tfId = it; selectedContent = MainContent.TRUE_FALSE_QUESTION_DETAILS },
                    navigateBack = { selectedContent = MainContent.WELCOME }
                )
                MainContent.TRUE_FALSE_QUESTION_DETAILS -> TrueFalseQuestionDetailsScreen(
                    navigateToEditTrueFalseQuestion = { selectedContent = MainContent.TRUE_FALSE_QUESTION_EDIT },
                    navigateBack = { selectedContent = MainContent.TRUE_FALSE_QUESTIONS },
                    trueFalseQuestionId = tfId,
                )
                MainContent.TRUE_FALSE_QUESTION_NEW -> NewTrueFalseQuestionScreen(
                    navigateBack = { selectedContent = MainContent.TRUE_FALSE_QUESTIONS },
                    onNavigateUp = { selectedContent = MainContent.TRUE_FALSE_QUESTIONS },
                )
                MainContent.TRUE_FALSE_QUESTION_EDIT -> TrueFalseQuestionEditScreen(
                    navigateBack = { selectedContent = MainContent.TRUE_FALSE_QUESTION_DETAILS },
                    trueFalseQuestionId = tfId,
                )

                MainContent.MULTIPLE_CHOICE_QUESTIONS -> MultipleChoiceQuestionListScreen(
                    addNewMultipleChoiceQuestion = { selectedContent = MainContent.MULTIPLE_CHOICE_QUESTION_NEW },
                    navigateToMultipleChoiceQuestionDetails = { multiId = it; selectedContent = MainContent.MULTIPLE_CHOICE_QUESTION_DETAILS },
                    navigateBack = { selectedContent = MainContent.WELCOME }
                )
                MainContent.MULTIPLE_CHOICE_QUESTION_DETAILS -> MultipleChoiceQuestionDetailsScreen(
                    navigateToEditMultipleChoiceQuestion = { selectedContent = MainContent.MULTIPLE_CHOICE_QUESTION_EDIT },
                    navigateBack = { selectedContent = MainContent.MULTIPLE_CHOICE_QUESTIONS },
                    multipleChoiceQuestionId = multiId,
                )
                MainContent.MULTIPLE_CHOICE_QUESTION_NEW -> NewMultipleChoiceQuestionScreen(
                    navigateBack = { selectedContent = MainContent.MULTIPLE_CHOICE_QUESTIONS },
                    onNavigateUp = { selectedContent = MainContent.MULTIPLE_CHOICE_QUESTIONS },
                )
                MainContent.MULTIPLE_CHOICE_QUESTION_EDIT -> MultipleChoiceQuestionEditScreen(
                    navigateBack = { selectedContent = MainContent.MULTIPLE_CHOICE_QUESTION_DETAILS },
                    multipleChoiceQuestionId = multiId,
                )

                MainContent.EXAMS -> ExamListScreen(
                    addNewExam = { selectedContent = MainContent.EXAM_NEW },
                    navigateToExamDetails = { examId = it; selectedContent = MainContent.EXAM_DETAILS },
                    navigateBack = { selectedContent = MainContent.WELCOME }
                )
                MainContent.EXAM_DETAILS -> ExamDetailsScreen(
                    navigateToEditMultipleChoiceQuestion = { selectedContent = MainContent.EXAM_EDIT },
                    navigateBack = { selectedContent = MainContent.EXAMS },
                    examId = examId,
                )
                MainContent.EXAM_NEW -> NewExamScreen(
                    navigateBack = { selectedContent = MainContent.EXAMS },
                    onNavigateUp = { selectedContent = MainContent.EXAMS },
                )
                MainContent.EXAM_EDIT -> ExamEditScreen(
                    navigateBack = { selectedContent = MainContent.EXAM_DETAILS },
                    examId = examId,
                )

                MainContent.SUBMISSIONS -> ExamListScreen(
                    addNewExam = { selectedContent = MainContent.EXAM_NEW },
                    navigateToExamDetails = { submissionId = it; selectedContent = MainContent.SUBMISSION },
                    navigateBack = { selectedContent = MainContent.WELCOME }
                )
                MainContent.SUBMISSION -> SubmissionScreen(
                    examId = submissionId,
                    navigateBack = { selectedContent = MainContent.SUBMISSIONS },
                    navigateBackCamera = { selectedContent = MainContent.SUBMISSIONS },
                )
                MainContent.EXPORT_EXAMS -> ExamListScreen(
                    addNewExam = { selectedContent = MainContent.EXAM_NEW },
                    navigateToExamDetails = { exportId = it; selectedContent = MainContent.EXPORT_EXAM },
                    navigateBack = { selectedContent = MainContent.WELCOME }
                )
                MainContent.EXPORT_EXAM -> ExportExamDetailsScreen(
                    examId = exportId,
                    navigateBack = { selectedContent = MainContent.EXPORT_EXAMS }
                )

            }
        }
    }
}



@Composable
fun DrawerButton(
    text: String,
    onClick: () -> Unit,
    scaffoldState: ScaffoldState,
    coroutineScope: CoroutineScope
) {
    OutlinedButton(
        onClick = {
            onClick()
            coroutineScope.launch { scaffoldState.drawerState.close() }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text)
    }
}

enum class MainContent {
    WELCOME, TOPICS, POINTS, TRUE_FALSE_QUESTIONS, MULTIPLE_CHOICE_QUESTIONS, EXAMS, SUBMISSIONS, EXPORT_EXAMS,
    TOPIC_DETAILS, POINT_DETAILS, TRUE_FALSE_QUESTION_DETAILS, MULTIPLE_CHOICE_QUESTION_DETAILS, EXAM_DETAILS,
    TOPIC_NEW, POINT_NEW, TRUE_FALSE_QUESTION_NEW, MULTIPLE_CHOICE_QUESTION_NEW, EXAM_NEW,
    TOPIC_EDIT, POINT_EDIT, TRUE_FALSE_QUESTION_EDIT, MULTIPLE_CHOICE_QUESTION_EDIT, EXAM_EDIT,
    SUBMISSION, EXPORT_EXAM
}

