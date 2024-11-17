package hu.bme.aut.android.examapp.ui.exam

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import examapp.composeapp.generated.resources.Res
import examapp.composeapp.generated.resources.*
import hu.bme.aut.android.examapp.service.api.dto.*
import hu.bme.aut.android.examapp.ui.components.DropDownList
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.multiplechoicequestion.MultipleChoiceQuestionDetails
import hu.bme.aut.android.examapp.ui.theme.Green
import hu.bme.aut.android.examapp.ui.theme.GreenLight
import hu.bme.aut.android.examapp.ui.theme.PaleDogwood
import hu.bme.aut.android.examapp.ui.theme.Purple40
import hu.bme.aut.android.examapp.ui.theme.Seashell
import hu.bme.aut.android.examapp.ui.truefalsequestion.DeleteConfirmationDialog
import hu.bme.aut.android.examapp.ui.truefalsequestion.TrueFalseQuestionDetails
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamDetails
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamDetailsScreenUiState
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamDetailsUiState
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamDetailsViewModel
import hu.bme.aut.android.examapp.ui.viewmodel.multiplechoicequestion.toMultipleChoiceQuestionDetails
import hu.bme.aut.android.examapp.ui.viewmodel.truefalsequestion.toTrueFalseQuestionDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExamDetailsScreen(
    navigateToEditMultipleChoiceQuestion: (String) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    examId: String,
    examViewModel: ExamDetailsViewModel = viewModel { ExamDetailsViewModel(examId) }
) {
    LaunchedEffect(examId) {
        examViewModel.setId(examId)
    }
    when(examViewModel.examDetailsScreenUiState){
        is ExamDetailsScreenUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is ExamDetailsScreenUiState.Success -> {
            ExamDetailsDetailsScreenUiState(
                exam =  (examViewModel.examDetailsScreenUiState as ExamDetailsScreenUiState.Success).exam,
                navigateToEditMultipleChoiceQuestion = navigateToEditMultipleChoiceQuestion,
                examViewModel = examViewModel,
                modifier = modifier,
                navigateBack = navigateBack,
                examId = examId,
            )
        }
        is ExamDetailsScreenUiState.Error -> Text(text = ExamDetailsScreenUiState.Error.errorMessage.ifBlank { "Unexpected error " })
    }

    LaunchedEffect(key1 = Unit) {
        examViewModel.getExam(examViewModel.examId)
    }
}

@Composable
fun ExamDetailsDetailsScreenUiState(
    exam: ExamDto,
    navigateToEditMultipleChoiceQuestion: (String) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    examId: String,
    examViewModel: ExamDetailsViewModel
){
    val  examUiState = examViewModel.uiState//.collectAsState()
    var tabPage by remember { mutableStateOf(Type.trueFalseQuestion) }
    val coroutineScope = rememberCoroutineScope()
    val backgroundColor by animateColorAsState(
        if (tabPage == Type.trueFalseQuestion) Seashell else GreenLight,
        label = "background color"
    )

    Scaffold(
        topBar = {
            Column {
                TopAppBarContent(stringResource(Res.string.exam_details), navigateBack)
                SearchBar(
                    backgroundColor = backgroundColor,
                    tabPage = tabPage,
                    onTabSelected = { tabPage = it },
                    topics = examViewModel.topicList,
                    trueFalse =  examViewModel.trueFalseList,
                    multipleChoice =  examViewModel.multipleChoiceList,
                    questions = examUiState.examDetails.questionList,
                    examTopic = examUiState.examDetails.topicId,
                ){
                    coroutineScope.launch {
                        when (tabPage) {
                            Type.trueFalseQuestion -> examViewModel.saveQuestion(tabPage.ordinal, it)
                            Type.multipleChoiceQuestion -> examViewModel.saveQuestion(tabPage.ordinal, it)
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            SmallFloatingActionButton(
                onClick = { navigateToEditMultipleChoiceQuestion(examUiState.examDetails.id) },
                shape = MaterialTheme.shapes.medium,
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(Res.string.exam_info),
                )
            }
        },
        bottomBar = {
            Spacer(modifier = Modifier.padding(16.dp))
        },
        modifier = modifier
    ) { innerPadding ->
        ExamDetailsBody(
            exam = examUiState.examDetails,
            topicList = examViewModel.topicList,
            pointList = examViewModel.pointList,
            modifier = Modifier.padding(innerPadding),
            tabPage = tabPage,
            examId = examId,
            navigateBack = navigateBack,
        )
    }
}


@Composable
private fun SearchBar(
    backgroundColor: Color,
    tabPage: Type,
    examTopic: String,
    onTabSelected: (tabPage: Type) -> Unit,
    topics: List<TopicDto>,
    trueFalse: List<TrueFalseQuestionDto>,
    multipleChoice: List<MultipleChoiceQuestionDto>,
    questions: List<Question>,
    onAddQuestion: (String) -> Unit,
) {
    //val context = LocalContext.current

    Column(modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        TabRow(
            selectedTabIndex = tabPage.ordinal,
            containerColor = backgroundColor,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            indicator = { tabPositions ->
                QuestionTabIndicator(tabPositions, tabPage)
            }
        ) {
            QuestionTab(
                icon = Icons.Default.Check,
                title = stringResource(Res.string.true_false),
                onClick = { onTabSelected(Type.trueFalseQuestion) }
            )
            QuestionTab(
                icon = Icons.AutoMirrored.Filled.List,
                title = stringResource(Res.string.multiple),
                onClick = { onTabSelected(Type.multipleChoiceQuestion) }
            )
        }

        var topicFilter by rememberSaveable { mutableStateOf("") }
        var topicId by rememberSaveable { mutableStateOf("") }
        DropDownList(
            name = stringResource(Res.string.topic),
            items = topics.map { it.topic },
            onChoose = { topicName ->
                topicFilter = topicName
                topicId = topics.filter { it.topic.contains(topicFilter) }.map { it.uuid }.first()
            }
        )

        var question by rememberSaveable { mutableStateOf("") }
        when(tabPage){
            Type.trueFalseQuestion -> {
            val tFQuestions: List<TrueFalseQuestionDto> = questions.filterIsInstance<TrueFalseQuestionDto>()
                DropDownList(
                    name = stringResource(Res.string.question),
                    items = trueFalse
                        .filterNot{ tFQuestions.contains(it) }
                        .filter{ it.topic == if(topicId=="") examTopic else topicId }
                        .map { it.question},
                    onChoose = { question = it }
                )
            }
            Type.multipleChoiceQuestion -> {
                val mCQuestions: List<MultipleChoiceQuestionDto> = questions.filterIsInstance<MultipleChoiceQuestionDto>()
                DropDownList(
                    name = stringResource(Res.string.question),
                    items = multipleChoice
                        .filterNot { mCQuestions.contains(it) }
                        .filter{ it.topic == if(topicId=="") examTopic else topicId }
                        .map { it.question },
                    onChoose = { question = it }
                )
            }
        }

        OutlinedButton(
            onClick = {
                onAddQuestion(question)

                /*val bundle = Bundle()
                bundle.putString("add_question", question)

                FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SELECT_ITEM){
                    param(FirebaseAnalytics.Param.ITEM_NAME, question)
                    param(FirebaseAnalytics.Param.SUCCESS, "true")
                }

                FirebaseAnalytics.getInstance(context)
                    .logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)*/
                question = ""
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
            enabled = question.isNotEmpty()
        ) {
            Text(text = stringResource(Res.string.add_question))
        }

    }

}

@Composable
private fun QuestionTabIndicator(
    tabPositions: List<TabPosition>,
    tabPage: Type,
) {

    val transition = updateTransition(
        tabPage,
        label = "Tab indicator"
    )
    val indicatorLeft by transition.animateDp(
        transitionSpec = {
            if (Type.trueFalseQuestion isTransitioningTo Type.multipleChoiceQuestion) {
                spring(stiffness = Spring.StiffnessVeryLow)
            } else {
                spring(stiffness = Spring.StiffnessMedium)
            }
        },
        label = "Indicator left"
    ) { page ->
        tabPositions[page.ordinal].left
    }
    val indicatorRight by transition.animateDp(
        transitionSpec = {
            if (Type.trueFalseQuestion isTransitioningTo Type.multipleChoiceQuestion) {
                spring(stiffness = Spring.StiffnessMedium)
            } else {
                spring(stiffness = Spring.StiffnessVeryLow)
            }
        },
        label = "Indicator right"
    ) { page ->
        tabPositions[page.ordinal].right
    }
    val color by transition.animateColor(
        label = "Border color"
    ) { page ->
        if (page == Type.trueFalseQuestion) PaleDogwood else Green
    }
    Box(
        Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.BottomStart)
            .offset(x = indicatorLeft)
            .width(indicatorRight - indicatorLeft)
            .padding(4.dp)
            .fillMaxSize()
            .border(
                BorderStroke(2.dp, color),
                RoundedCornerShape(4.dp)
            )
    )
}

@Composable
private fun QuestionTab(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title)
    }
}

@Composable
fun ExamDetailsBody(
    exam: ExamDetails,
    topicList: List<TopicDto>,
    pointList: List<PointDto>,
    tabPage: Type,
    examId: String,
    modifier: Modifier = Modifier,
    examViewModel: ExamDetailsViewModel = viewModel { ExamDetailsViewModel(examId) },//viewModel(factory = AppViewModelProvider.Factory),
    navigateBack: () -> Unit
){
    val coroutineScope = rememberCoroutineScope()
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
    var lazyQuestions by remember { mutableStateOf(exam.questionList) }
    lazyQuestions = exam.questionList.toMutableList()
    val state = rememberReorderableLazyListState(onMove = { from, to ->
        lazyQuestions = lazyQuestions.toMutableList().apply {
            add(to.index, removeAt(from.index))
            coroutineScope.launch {
                examViewModel.saveQuestionOrdering(lazyQuestions)
                examViewModel.uiState = ExamDetailsUiState(exam.copy(questionList = lazyQuestions))
            }
        }
    })
    Column{
        LazyColumn(
            state = state.listState,
            modifier = modifier
                .reorderable(state = state)
                .detectReorderAfterLongPress(state)
        ) {
            items(lazyQuestions, {if(it is TrueFalseQuestionDto) "${it.type}~${it.uuid}" else if(it is MultipleChoiceQuestionDto) "${it.type}~${it.uuid}" else 0}) { question ->
                ReorderableItem(state, key = question) { isDragging ->
                    val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp, label = "")
                    Column(
                        modifier = Modifier
                            .shadow(elevation.value)
                    ) {
                        ExpandableQuestionItem(
                            topicList = topicList,
                            pointList = pointList,
                            question = question,
                            examViewModel = examViewModel
                        )
                    }
                }
            }
        }
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
                    coroutineScope.launch { examViewModel.deleteExam() }
                    navigateBack()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(16.dp)
            )
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableQuestionItem(
    topicList: List<TopicDto>,
    pointList: List<PointDto>,
    question: Question,
    examViewModel: ExamDetailsViewModel,
){
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f, label = ""
    )
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        shape = shape,
        onClick = {
            expandedState = !expandedState
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(if(question.typeOrdinal == Type.trueFalseQuestion.ordinal) PaleDogwood else Green)
        ) {
            Column(
                modifier = Modifier
                    .weight(6f)
            ) {
                when (question.typeOrdinal) {
                    Type.trueFalseQuestion.ordinal -> {
                        val trueFalseQuestion = question as TrueFalseQuestionDto
                        if (expandedState) {
                            TrueFalseQuestionDetails(
                                trueFalseQuestion = trueFalseQuestion.toTrueFalseQuestionDetails(
                                    topicName = if (trueFalseQuestion.topic != "")
                                        topicList.first { it.uuid == trueFalseQuestion.topic }.topic
                                    else "",
                                    pointName = if (trueFalseQuestion.point != "")
                                        pointList.first { it.uuid == trueFalseQuestion.point }.type
                                    else ""
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = PaleDogwood,
                                    contentColor = Purple40
                                )
                            )
                            RemoveButton(coroutineScope, examViewModel, question)
                        } else {
                            CollapsedQuestion(
                                question = trueFalseQuestion.question,
                                containerColor = PaleDogwood, contentColor = Purple40
                                )
                        }
                    }

                    Type.multipleChoiceQuestion.ordinal -> {
                        val multipleChoiceQuestion = question as MultipleChoiceQuestionDto
                        if (expandedState) {
                            MultipleChoiceQuestionDetails(
                                multipleChoiceQuestion = multipleChoiceQuestion.toMultipleChoiceQuestionDetails(
                                    topicName = if (multipleChoiceQuestion.topic != "")
                                        topicList.first { it.uuid == multipleChoiceQuestion.topic }.topic
                                    else "",
                                    pointName = if (multipleChoiceQuestion.point != "")
                                        pointList.first { it.uuid == multipleChoiceQuestion.point }.type
                                    else ""
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Green,
                                    contentColor = Purple40
                                )
                            )
                            RemoveButton(coroutineScope, examViewModel, question)
                        } else {
                            CollapsedQuestion(
                                question =  multipleChoiceQuestion.question,
                                containerColor = Green, contentColor = Purple40
                            )
                        }
                    }


                }

            }
            IconButton(
                modifier = Modifier
                    .weight(1f)
                    .alpha(0.2f)
                    .rotate(rotationState),
                onClick = {
                    expandedState = !expandedState
                }) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Drop-Down Arrow"
                )
            }
        }
    }
}

@Composable
private fun RemoveButton(
    coroutineScope: CoroutineScope,
    examViewModel: ExamDetailsViewModel,
    question: Question
) {
    OutlinedButton(
        onClick = {
            coroutineScope.launch {
                examViewModel.removeQuestion(question)
            }
        },
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(Res.string.remove_question))
    }
}

@Composable
private fun CollapsedQuestion(
    question: String,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    colors: CardColors = CardDefaults.cardColors(
        containerColor = containerColor,
        contentColor = contentColor
    )
){
    Card(
        modifier = Modifier.background(containerColor),
        colors = colors,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(text = stringResource(Res.string.question))
                Spacer(modifier = Modifier.weight(1f))
                Text(text = question, fontWeight = FontWeight.Bold)
            }
        }
    }
}

