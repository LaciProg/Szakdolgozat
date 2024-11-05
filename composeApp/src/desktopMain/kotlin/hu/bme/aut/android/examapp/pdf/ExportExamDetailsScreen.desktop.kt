package hu.bme.aut.android.examapp.pdf

import androidx.annotation.StringRes
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import examapp.composeapp.generated.resources.Res
import examapp.composeapp.generated.resources.*
import hu.bme.aut.android.examapp.api.dto.MultipleChoiceQuestionDto
import hu.bme.aut.android.examapp.api.dto.PointDto
import hu.bme.aut.android.examapp.api.dto.Question
import hu.bme.aut.android.examapp.api.dto.TrueFalseQuestionDto
import hu.bme.aut.android.examapp.ui.components.ExportedMultipleChoiceQuestion
import hu.bme.aut.android.examapp.ui.components.ExportedTrueFalseQuestion
import hu.bme.aut.android.examapp.ui.components.TopAppBarContent
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamDetailsViewModel
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
actual fun ExportExamDetailsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier,
    examId: String,
    examViewModel: ExamDetailsViewModel
) {
    Scaffold(
        topBar = {
            TopAppBarContent(examViewModel.uiState.examDetails.name, navigateBack)
                 },
        ){innerPadding ->
            ExportExamDetailsScreen(navigateBack = navigateBack, modifier.padding(innerPadding), examViewModel)
    }
}


fun saveExamDetailsAsPdf(
    examName: String,
    questions: List<Question>,
    pointList: List<PointDto>,
    maxPoints: Double,
    gradeBoundaries: List<Pair<String, String>>,
    outputFile: File
) {
    // Create a new PDF document
    val document = PDDocument()
    val page = PDPage()
    document.addPage(page)

    // Define text properties
    val titleFont = PDType1Font.HELVETICA_BOLD
    val regularFont = PDType1Font.HELVETICA
    val titleFontSize = 18f
    val regularFontSize = 12f

    val startX = 20f
    var currentY = page.mediaBox.height - 40f
    val leftColX = startX
    val rightColX = 300f
    val pageWidth = page.mediaBox.width
    val cellHeight = 40f
    var cellYPosition = currentY - 60f
    val lineHeight = 20f // Define line height
    var yPosition = currentY - 60f // Define yPosition for the questions section

    PDPageContentStream(document, page).use { contentStream ->
        contentStream.setFont(titleFont, titleFontSize)

        // Header
        contentStream.beginText()
        contentStream.newLineAtOffset(startX, currentY)
        contentStream.showText("Exam: $examName")
        contentStream.endText()

        contentStream.setFont(regularFont, regularFontSize)
        contentStream.beginText()
        contentStream.newLineAtOffset(300f, currentY)
        contentStream.showText("Date: ________________________")
        contentStream.endText()

        contentStream.beginText()
        contentStream.newLineAtOffset(pageWidth - 60f, currentY)
        contentStream.showText("Page 1")
        contentStream.endText()

        currentY -= 40f
        contentStream.beginText()
        contentStream.newLineAtOffset(startX, currentY)
        contentStream.showText("Name: _______________________")
        contentStream.endText()

        contentStream.beginText()
        contentStream.newLineAtOffset(300f, currentY)
        contentStream.showText("Neptun: ____________________")
        contentStream.endText()

        // Table Header: "Grade Boundaries" and "Total Points"
        cellYPosition -= cellHeight
        contentStream.setFont(titleFont, regularFontSize)
        contentStream.beginText()
        contentStream.newLineAtOffset(leftColX + 10, cellYPosition + 25)
        contentStream.showText("Grade Boundaries")
        contentStream.endText()

        contentStream.beginText()
        contentStream.newLineAtOffset(rightColX + 10, cellYPosition + 25)
        contentStream.showText("Total Points: $maxPoints")
        contentStream.endText()

        // Table Header Borders
        contentStream.setStrokingColor(java.awt.Color.BLACK)
        contentStream.setLineWidth(2f)
        contentStream.addRect(leftColX, cellYPosition, rightColX - leftColX, cellHeight)
        contentStream.addRect(rightColX, cellYPosition, pageWidth - rightColX - 20f, cellHeight)
        contentStream.stroke()

        cellYPosition -= cellHeight

        // "Reached Points" and "Percent" section as a larger rectangle
        contentStream.beginText()
        contentStream.newLineAtOffset(rightColX + 10, cellYPosition + 25)
        contentStream.showText("Reached Points: ___________")
        contentStream.endText()

        contentStream.beginText()
        contentStream.newLineAtOffset(rightColX + 10, cellYPosition - 15)
        contentStream.showText("Percent: ___________")
        contentStream.endText()

        // Draw outer rectangle for "Reached Points" and "Percent"
        contentStream.addRect(rightColX, cellYPosition - cellHeight, pageWidth - rightColX - 20f, cellHeight * 2)
        contentStream.stroke()

        // Draw Table Rows and Borders
        contentStream.setFont(regularFont, regularFontSize)
        for ((range, grade) in gradeBoundaries) {
            contentStream.beginText()
            contentStream.newLineAtOffset(leftColX + 10, cellYPosition + 25)
            contentStream.showText(range)
            contentStream.endText()

            contentStream.beginText()
            contentStream.newLineAtOffset(leftColX + 150, cellYPosition + 25)
            contentStream.showText(grade)
            contentStream.endText()

            contentStream.addRect(leftColX, cellYPosition, rightColX - leftColX, cellHeight)
            contentStream.stroke()

            cellYPosition -= cellHeight
        }

        yPosition = cellYPosition - lineHeight * 2
        val questionXOffset = 30f  // Adjust this value to provide more space between table and questions
        val answerXOffset = 30f    // Offset for answers (e.g., True/False or multiple-choice options)

        // Start "Questions" title
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, titleFontSize)
        contentStream.beginText()
        contentStream.newLineAtOffset(questionXOffset, yPosition)
        contentStream.showText("Questions")
        contentStream.endText()

        yPosition -= lineHeight

        // Add each question with points
        questions.forEachIndexed { index, question ->
            val questionText = "${index + 1}. ${question.questionText()}"
            val pointText = "Points: ${pointList.find { it.uuid == question.questionPoint() }?.point ?: 0.0} \\ "

            // Check if we need a new page
            if (yPosition < 100f) {
                contentStream.close()
                val newPage = PDPage()
                document.addPage(newPage)
                PDPageContentStream(document, newPage).use { newPageStream ->
                    yPosition = 750f
                }
            }

            // Display question text with adjusted X offset
            contentStream.setFont(PDType1Font.HELVETICA, regularFontSize)
            contentStream.beginText()
            contentStream.newLineAtOffset(questionXOffset, yPosition)
            contentStream.showText(questionText)
            contentStream.endText()

            // Display point text aligned to the right
            contentStream.beginText()
            contentStream.newLineAtOffset(500f, yPosition)
            contentStream.showText(pointText)
            contentStream.endText()

            yPosition -= lineHeight * 1.5f

            // Format answers based on question type
            if (question is TrueFalseQuestionDto) {
                // True/False question layout with adjusted answer offset
                contentStream.beginText()
                contentStream.newLineAtOffset(answerXOffset, yPosition)
                contentStream.showText("True")
                contentStream.endText()

                contentStream.beginText()
                contentStream.newLineAtOffset(answerXOffset + 60f, yPosition)  // Offset for 'False' option
                contentStream.showText("False")
                contentStream.endText()

                yPosition -= lineHeight * 1.5f
            } else if (question is MultipleChoiceQuestionDto) {
                // Multiple-choice answers layout with adjusted answer offset
                question.answers.forEachIndexed { answerIndex, answer ->
                    // Check if we need a new page for long answer lists
                    if (yPosition < 100f) {
                        contentStream.close()
                        val newPage = PDPage()
                        document.addPage(newPage)
                        PDPageContentStream(document, newPage).use { newPageStream ->
                            yPosition = 750f
                        }
                    }

                    // Display each multiple-choice option with labels like A, B, etc.
                    val answerLabel = "${'A' + answerIndex}"

                    contentStream.beginText()
                    contentStream.newLineAtOffset(answerXOffset, yPosition)
                    contentStream.showText(answerLabel)
                    contentStream.endText()
                    contentStream.beginText()
                    contentStream.newLineAtOffset(answerXOffset + 60f, yPosition)
                    contentStream.showText(answer)
                    contentStream.endText()
                    yPosition -= lineHeight * 1.5f
                }
            }
        }
    }

    document.save(outputFile)
    document.close()
}


// Extension function to get question text based on type
fun Question.questionText(): String = when (this) {
    is TrueFalseQuestionDto -> question
    is MultipleChoiceQuestionDto -> question
    else -> ""
}

fun Question.questionPoint(): String = when (this) {
    is TrueFalseQuestionDto -> point
    is MultipleChoiceQuestionDto -> point
    else -> ""
}

@Composable
fun ExportExamDetailsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    examViewModel: ExamDetailsViewModel
) {
    val gradeBoundaries = listOf(
        stringResource(Res.string.one_percent) to stringResource(Res.string.one_grade),
        stringResource(Res.string.two_percent) to stringResource(Res.string.two_grade),
        stringResource(Res.string.three_percent) to stringResource(Res.string.three_grade),
        stringResource(Res.string.four_percent) to stringResource(Res.string.four_grade),
        stringResource(Res.string.five_percent) to stringResource(Res.string.five_grade)
    )

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    // Open file save dialog for desktop environment
                    val file = choosePdfFileLocation()
                    file?.let {
                        // Call save function if file location was selected
                        saveExamDetailsAsPdf(
                            examName = examViewModel.uiState.examDetails.name,
                            questions = examViewModel.uiState.examDetails.questionList,
                            pointList = examViewModel.pointList,
                            maxPoints = getExamPointList(examViewModel.uiState.examDetails.questionList, examViewModel.pointList).sum(),
                            gradeBoundaries = gradeBoundaries,
                            outputFile = it
                        )
                    }
                },
                icon = { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Export pdf") },
                text = { Text("Export to pdf", color = Color.White) }
            )
        },
    ) { padding ->
        ExportExamDetailsBodyView(
            examName = examViewModel.uiState.examDetails.name,
            questions = examViewModel.uiState.examDetails.questionList,
            pointList = examViewModel.pointList,
            modifier = modifier.padding(padding)
        )
    }
}

// Desktop-specific function to prompt user to select a .pdf file location
private fun choosePdfFileLocation(): File? {
    val dialog = FileDialog(null as Frame?, "Save As", FileDialog.SAVE)
    dialog.file = "*.pdf" // Suggests .pdf extension
    dialog.isVisible = true

    return if (dialog.file != null) {
        var file = File(dialog.directory, dialog.file)
        // Ensure file has .pdf extension
        if (!file.name.endsWith(".pdf", ignoreCase = true)) {
            file = File(file.absolutePath + ".pdf")
        }
        file
    } else {
        null
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
fun ExportExamDetailsBodyView(
    examName: String,
    questions: List<Question>,
    pointList: List<PointDto>,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
) {
    // Logging can be used on desktop if needed
    println("ExportExamDetailsBodyView: examName: $examName, questions: $questions, pointList: $pointList")

    // Directly use ExportExamDetailsBody to display the content
    if (examName.isNotEmpty() && questions.isNotEmpty() && pointList.isNotEmpty()) {
        ExportExamDetailsBody(
            examName = examName,
            questions = questions,
            pointList = pointList,
            modifier = modifier
        )
    }
}

@Composable
fun ExportExamDetailsBody(
    examName: String,
    questions: List<Question>,
    pointList: List<PointDto>,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Header(modifier, examName)
            Spacer(modifier = modifier.size(16.dp))
            Points(modifier, maxPoints = getExamPointList(questions, pointList).sum())
            Text(stringResource(Res.string.question))  // Replace stringResource for Desktop compatibility
            Spacer(modifier = modifier.size(16.dp))
            questions.forEachIndexed { index, question ->
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
                value = stringResource(Res.string.total_points)+"$maxPoints",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.size(height = 50.dp, width = 200.dp)
            )
            OutlinedTextField(
                value = stringResource(Res.string.reached_points),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.size(height = 50.dp, width = 200.dp)
            )
            OutlinedTextField(
                value = stringResource(Res.string.percent),
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
            value = stringResource(Res.string.grade_boundaries),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.size(height = 50.dp, width = 250.dp)
        )
        GradeBoundariesTableLine(percent = Res.string.one_percent, grade = Res.string.one_grade)
        GradeBoundariesTableLine(percent = Res.string.two_percent, grade = Res.string.two_grade)
        GradeBoundariesTableLine(percent = Res.string.three_percent, grade = Res.string.three_grade)
        GradeBoundariesTableLine(percent = Res.string.four_percent, grade = Res.string.four_grade)
        GradeBoundariesTableLine(percent = Res.string.five_percent, grade = Res.string.five_grade)
    }

}

@Composable
private fun GradeBoundariesTableLine(@StringRes percent: StringResource, @StringRes grade: StringResource) {
    Row(Modifier.size(height = 50.dp, width = 250.dp)) {
        OutlinedTextField(
            value = stringResource(percent),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.weight(3.5f)
        )
        OutlinedTextField(
            value = stringResource(grade),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.weight(5f)
        )
    }
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
                value = stringResource(Res.string.date),
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
                value = stringResource(Res.string.name),
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
                value = stringResource(Res.string.neptun_code),
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