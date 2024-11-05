package hu.bme.aut.android.examapp.ui

import androidx.compose.runtime.Composable

@Composable
expect fun MainScreen(
    navigateToTopicList: () -> Unit,
    navigateToPointList: () -> Unit,
    navigateToTrueFalseQuestionList: () -> Unit,
    navigateToMultipleChoiceQuestionList: () -> Unit,
    navigateToExamList: () -> Unit,
    navigateToExportExamList: () -> Unit,
    navigateToSubmission: () -> Unit,
    onSignOut: () -> Unit,
)