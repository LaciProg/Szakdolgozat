package hu.bme.aut.android.examapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import hu.bme.aut.android.examapp.ui.MainScreen
import hu.bme.aut.android.examapp.ui.point.PointListScreen
import hu.bme.aut.android.examapp.ui.topic.TopicListScreen

@Composable
actual fun ExamNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = ExamDestination.MainScreenDestination.route,
        modifier = modifier
    ) {
        composable(route = ExamDestination.MainScreenDestination.route) {
            MainScreen(
                navigateToTopicList = { navController.navigate(ExamDestination.TopicListDestination.route) },
                navigateToPointList = { navController.navigate(ExamDestination.PointListDestination.route) },
                navigateToTrueFalseQuestionList = { navController.navigate(ExamDestination.TrueFalseQuestionListDestination.route) },
                navigateToMultipleChoiceQuestionList = { navController.navigate(ExamDestination.MultipleChoiceQuestionListDestination.route) },
                navigateToExamList = { navController.navigate(ExamDestination.ExamListDestination.route) },
                navigateToExportExamList = { navController.navigate(ExamDestination.ExportExamListDestination.route) },
                navigateToSubmission = { navController.navigate(ExamDestination.SubmissionListDestination.route) },
                onSignOut = {
                    navController.popBackStack(ExamDestination.LoginScreenDestination.route, inclusive = true)
                    navController.navigate(ExamDestination.LoginScreenDestination.route)
                }
            )
        }

        // Define each list screen's composable
        composable(route = ExamDestination.TopicListDestination.route) {
            TopicListScreen(
                addNewTopic = { /* handle adding a new topic */ },
                navigateToTopicDetails = { /* handle navigation to topic details */ },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(route = ExamDestination.PointListDestination.route) {
            PointListScreen(
                addNewPoint = { /* handle adding a new topic */ },
                navigateToPointDetails = { /* handle navigation to topic details */ },
                navigateBack = { navController.popBackStack() }
            )
        }

        // Add other list screens similarly
    }
}