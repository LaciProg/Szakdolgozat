package hu.bme.aut.android.examapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import examapp.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import examapp.composeapp.generated.resources.*

@Composable
actual fun MainScreen(
    navigateToTopicList: () -> Unit,
    navigateToPointList: () -> Unit,
    navigateToTrueFalseQuestionList: () -> Unit,
    navigateToMultipleChoiceQuestionList: () -> Unit,
    navigateToExamList: () -> Unit,
    navigateToExportExamList: () -> Unit,
    navigateToSubmission: () -> Unit,
    onSignOut: () -> Unit,
){
    Scaffold {paddingValues ->
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
            OutlinedButton(
                onClick = { navigateToExportExamList() },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.export_exams))
            }
        }

    }

}