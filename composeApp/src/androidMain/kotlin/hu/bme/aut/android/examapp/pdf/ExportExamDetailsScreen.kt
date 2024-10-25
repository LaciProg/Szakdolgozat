package hu.bme.aut.android.examapp.pdf

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.SavedStateHandle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import hu.bme.aut.android.examapp.R
import hu.bme.aut.android.examapp.api.dto.MultipleChoiceQuestionDto
import hu.bme.aut.android.examapp.api.dto.PointDto
import hu.bme.aut.android.examapp.api.dto.Question
import hu.bme.aut.android.examapp.api.dto.TrueFalseQuestionDto
import hu.bme.aut.android.examapp.ui.components.ExportedMultipleChoiceQuestion
import hu.bme.aut.android.examapp.ui.components.ExportedTrueFalseQuestion
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamDetailsViewModel
import java.io.IOException


fun saveExamDetailsAsPdf(
    context: Context,
    examName: String,
    questions: List<Question>,
    pointList: List<PointDto>,
    maxPoints: Double
) {
    val pdfDocument = PdfDocument()
    val paint = Paint().apply { textSize = 12f }
    val titlePaint = Paint().apply {
        textSize = 18f
        isFakeBoldText = true
    }
    val tablePaint = Paint().apply {
        textSize = 12f
        isFakeBoldText = false
    }

    val linePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    val pageWidth = 595 // A4 size width in points
    val pageHeight = 842 // A4 size height in points
    var currentYPosition = 40
    var pageNumber = 1

    // Function to create a new page
    fun createNewPage(): PdfDocument.Page {
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Add header with exam name and page number
        canvas.drawText("Exam: $examName", 20f, 30f, titlePaint)
        canvas.drawText("Date: ________________________", 300f, currentYPosition.toFloat(), paint)
        canvas.drawText("Page $pageNumber", (pageWidth - 60).toFloat(), 30f, paint)

        pageNumber++
        currentYPosition = 100
        return page
    }

    // Create the first page
    var page = createNewPage()
    var canvas = page.canvas

    // --- Header Section ---
    canvas.drawText("Name: _______________________", 20f, currentYPosition.toFloat(), paint)
    canvas.drawText("Neptun: ____________________", 300f, currentYPosition.toFloat(), paint)
    currentYPosition += 60

    // --- Grade Boundaries Table ---
    // Column titles
    val leftColX = 20f
    val rightColX = 300f
    val cellHeight = 40f
    var cellYPosition = currentYPosition.toFloat()

    // Draw table headers
    canvas.drawText("Grade Boundaries", leftColX + 10, cellYPosition + 25, tablePaint)
    canvas.drawText("Total Points: $maxPoints", rightColX + 10, cellYPosition + 25, tablePaint)

    // Draw table header borders
    canvas.drawRect(leftColX, cellYPosition, rightColX, cellYPosition + cellHeight, linePaint)
    canvas.drawRect(rightColX, cellYPosition, pageWidth - 20f, cellYPosition + cellHeight, linePaint)
    cellYPosition += cellHeight

    // Draw Reached Points and Percent
    canvas.drawText("Reached Points: ___________", rightColX + 10, cellYPosition + 25, tablePaint)
    canvas.drawText("Percent: ___________", rightColX + 10, cellYPosition + 65, tablePaint)

    // Draw borders for the Reached Points and Percent cells
    canvas.drawRect(rightColX, cellYPosition, pageWidth - 20f, cellYPosition + 2 * cellHeight, linePaint)

    // Table rows
    val tableRows = listOf(
        context.resources.getString(R.string.one_percent) to context.resources.getString(R.string.one_grade),
        context.resources.getString(R.string.two_percent) to context.resources.getString(R.string.two_grade),
        context.resources.getString(R.string.three_percent) to context.resources.getString(R.string.three_grade),
        context.resources.getString(R.string.four_percent) to context.resources.getString(R.string.four_grade),
        context.resources.getString(R.string.five_percent) to context.resources.getString(R.string.five_grade)
    )
    for ((range, grade) in tableRows) {
        canvas.drawText(range, leftColX + 10, cellYPosition + 25, tablePaint)
        canvas.drawText(grade, leftColX + 150, cellYPosition + 25, tablePaint)

        // Draw the row borders
        canvas.drawRect(leftColX, cellYPosition, rightColX, cellYPosition + cellHeight, linePaint)
        cellYPosition += cellHeight
    }

    currentYPosition = cellYPosition.toInt() + 60

    // Iterate through questions and add them to the PDF
    canvas.drawText("Questions", 20f, currentYPosition.toFloat(), titlePaint)
    currentYPosition += 40
    for ((index, question) in questions.withIndex()) {
        val questionText: String
        val pointText: String
        when (question) {
            is TrueFalseQuestionDto -> {
                questionText = "${index + 1}. ${question.question}"
                pointText = "Points: ${pointList.find { it.uuid == question.point }?.point ?: 0.0} \\ "
            }
            is MultipleChoiceQuestionDto -> {
                questionText = "${index + 1}. ${question.question}"
                pointText = "Points: ${pointList.find { it.uuid == question.point }?.point ?: 0.0} \\ "
            }
        }

        // Check if content fits on the page, otherwise create a new page
        if (currentYPosition + 40 > pageHeight) {
            pdfDocument.finishPage(page)
            page = createNewPage()
            canvas = page.canvas
        }

        // Draw question and point
        canvas.drawText(questionText, 20f, currentYPosition.toFloat(), paint)
        canvas.drawText(pointText, (pageWidth - 100).toFloat(), currentYPosition.toFloat(), paint)
        currentYPosition += 40

        // Draw additional details based on question type
        when (question) {
            is TrueFalseQuestionDto -> {
                canvas.drawText("True ${" ".repeat(20)} False", 20f, currentYPosition.toFloat(), paint)
                currentYPosition += 30
            }
            is MultipleChoiceQuestionDto -> {
                question.answers.forEachIndexed { index, answer ->
                    if (currentYPosition + 30 > pageHeight) {
                        pdfDocument.finishPage(page)
                        page = createNewPage()
                        canvas = page.canvas
                    }
                    canvas.drawText("${'A' + index} ${" ".repeat(20)} $answer", 20f, currentYPosition.toFloat(), paint)
                    currentYPosition += 30
                }
            }
        }
    }

    // Finish the last page
    pdfDocument.finishPage(page)

    // Save the PDF using MediaStore API for Android Q and above
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "$examName.pdf")
        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
    }

    val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

    try {
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
                Toast.makeText(context, "PDF saved successfully", Toast.LENGTH_LONG).show()
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Error saving PDF: ${e.message}", Toast.LENGTH_SHORT).show()
    } finally {
        pdfDocument.close()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun ExportExamDetailsScreen(    //IN navigation used
    navigateBack: () -> Unit,
    modifier: Modifier,
    savedStateHandle: SavedStateHandle,
    examViewModel: ExamDetailsViewModel
) {
    val context = LocalContext.current

    // Reference to the ComposeView
    val composeViewRef = remember { mutableStateOf<ComposeView?>(null) }

    Scaffold(
        topBar = {
            TopAppBarContent(examViewModel.uiState.examDetails.name, navigateBack)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    composeViewRef.value?.let { composeView ->
                        saveExamDetailsAsPdf(context,
                            examViewModel.uiState.examDetails.name,
                            examViewModel.uiState.examDetails.questionList,
                            examViewModel.pointList,
                            maxPoints = getExamPointList(examViewModel.uiState.examDetails.questionList, examViewModel.pointList).sum()
                        )
                    }
                },
                shape = RoundedCornerShape(18.dp),
                icon = { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Export pdf") },
                text = { Text("Export to pdf", color = Color.White) }
            )
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        backgroundColor = MaterialTheme.colors.primaryVariant
    ) { padding ->
        // Wrap your content in an AndroidView with ComposeView
        AndroidView(
            factory = { context ->
                ComposeView(context).apply {
                    // Save the reference of the ComposeView
                    composeViewRef.value = this
                    setContent {
                        ExportExamDetailsBodyView(
                            examName = examViewModel.uiState.examDetails.name,
                            questions = examViewModel.uiState.examDetails.questionList,
                            pointList = examViewModel.pointList,
                            modifier = modifier.padding(padding)
                        )
                    }
                }
            },
            modifier = Modifier.wrapContentSize()
        )
    }
}

@Composable
fun ExportExamDetailsBodyView(  //Calls the PDFExamView
    examName: String,
    questions: List<Question>,
    pointList: List<PointDto>,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
) {
    val context = LocalContext.current
    Log.d("ExportExamDetailsBodyView", "examName: $examName, questions: $questions, pointList: $pointList")

    // Use remember to store state
    val jetCaptureViewState = remember { mutableStateOf<PDFExamView?>(null) }

    // Use LaunchedEffect to observe state changes
    LaunchedEffect(examName, questions, pointList) {
        if (examName.isNotEmpty() && questions.isNotEmpty() && pointList.isNotEmpty()) {
            jetCaptureViewState.value = PDFExamView(
                context = context,
                examName = examName,
                questions = questions,
                pointList = pointList,
                modifier = modifier
            )
        }
    }

    // Update the composable view using AndroidView
    jetCaptureViewState.value?.let { pdfExamView ->
        AndroidView(
            factory = { pdfExamView },
            modifier = Modifier.wrapContentSize()
        )
    }
}

@Composable
fun TopBar(
    examName: String,
    modifier: Modifier = Modifier
) {

}

@Composable
fun ExportExamDetailsBody(  //Content you can see on the screen
    examName: String,
    questions: List<Question>,
    pointList: List<PointDto>,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
) {
    Card(modifier = modifier.fillMaxWidth()){
        Column(
            modifier = modifier.verticalScroll(rememberScrollState()).horizontalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween

        ){
            Header(modifier, examName)
            Spacer(modifier = modifier.size(16.dp))
            Points(modifier, maxPoints = getExamPointList(questions, pointList).sum())
            Text(text = stringResource(id = R.string.questions))
            Spacer(modifier = modifier.size(16.dp))
            for ((index, question) in questions.withIndex()) {
                when (question) {
                    is TrueFalseQuestionDto -> {
                        ExportedTrueFalseQuestion(
                            number = index + 1,
                            question = question.question,
                            point = pointList.find { it.uuid == question.point }?.point ?: 0.0
                        )
                    }
                    is MultipleChoiceQuestionDto -> {
                        ExportedMultipleChoiceQuestion(
                            number = index + 1,
                            question = question.question,
                            point = pointList.find { it.uuid == question.point }?.point ?: 0.0,
                            answers = question.answers,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Points(
    modifier: Modifier = Modifier,
    maxPoints: Double
){
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        PointTable(modifier = modifier)
        Spacer(modifier = modifier.size(16.dp))
        Column(modifier = Modifier.padding(top = 16.dp)){
            OutlinedTextField(
                value = stringResource(id = R.string.total_points)+"$maxPoints",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.size(height = 50.dp, width = 200.dp)
            )
            OutlinedTextField(
                value = stringResource(id = R.string.reached_points),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.size(height = 50.dp, width = 200.dp)
            )
            OutlinedTextField(
                value = stringResource(id = R.string.percent),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.size(height = 50.dp, width = 200.dp)
            )
        }
    }

}

@Composable
private fun PointTable(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 16.dp)) {
        OutlinedTextField(
            value = stringResource(id = R.string.grade_boundaries),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.size(height = 50.dp, width = 250.dp)
        )
        GradeBoundariesTableLine(percent = R.string.one_percent, grade = R.string.one_grade)
        GradeBoundariesTableLine(percent = R.string.two_percent, grade = R.string.two_grade)
        GradeBoundariesTableLine(percent = R.string.three_percent, grade = R.string.three_grade)
        GradeBoundariesTableLine(percent = R.string.four_percent, grade = R.string.four_grade)
        GradeBoundariesTableLine(percent = R.string.five_percent, grade = R.string.five_grade)
    }

}

@Composable
private fun GradeBoundariesTableLine(@StringRes percent: Int, @StringRes grade: Int) {
    Row(Modifier.size(height = 50.dp, width = 250.dp)) {
        OutlinedTextField(
            value = stringResource(id = percent),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.weight(3.5f)
        )
        OutlinedTextField(
            value = stringResource(id = grade),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.weight(5f)
        )
    }
}


private fun getExamPointList (
    questions: List<Question>,
    pointList: List<PointDto>
): List<Double> {
    val points: MutableList<Double> = mutableListOf()
    for (question in questions) {
        when (question) {
            is TrueFalseQuestionDto -> {
                points.add(pointList.find { it.uuid == question.point }?.point ?: 0.0)
            }

            is MultipleChoiceQuestionDto -> {
                points.add(pointList.find { it.uuid == question.point }?.point ?: 0.0)
            }

        }
    }
    return points
}

@Composable
private fun Header(modifier: Modifier, examName: String) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(modifier = modifier.padding(8.dp), horizontalArrangement = Arrangement.Center) {
            Text(text = examName)
        }
        Row(
            modifier = modifier.padding(8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ){
            BasicTextField(
                value = stringResource(id = R.string.date),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.size(height = 50.dp, width = 60.dp),
            )
            TextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.size(height = 50.dp, width = 150.dp),
                readOnly = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent
                )
            )
        }
        Row(
            modifier = modifier.padding(8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = stringResource(id = R.string.name),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.size(height = 50.dp, width = 60.dp),
            )
            TextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.size(height = 50.dp, width = 150.dp),
                readOnly = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.padding(8.dp))
            BasicTextField(
                value = stringResource(id = R.string.neptun_code),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.size(height = 50.dp, width = 60.dp),
            )
            TextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.size(height = 50.dp, width = 150.dp),
                readOnly = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent
                )
            )
        }
    }
}
