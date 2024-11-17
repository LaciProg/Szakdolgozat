package hu.bme.aut.android.examapp.service.navigation

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
        startDestination = hu.bme.aut.android.examapp.service.navigation.ExamDestination.MainScreenDestination.route,
        modifier = modifier
    ) {
        composable(route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.MainScreenDestination.route) {
            MainScreen(
                navigateToTopicList = { navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.TopicListDestination.route) },
                navigateToPointList = { navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.PointListDestination.route) },
                navigateToTrueFalseQuestionList = { navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.TrueFalseQuestionListDestination.route) },
                navigateToMultipleChoiceQuestionList = { navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.MultipleChoiceQuestionListDestination.route) },
                navigateToExamList = { navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExamListDestination.route) },
                navigateToExportExamList = { navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExportExamListDestination.route) },
                navigateToSubmission = { navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.SubmissionListDestination.route) },
                onSignOut = {
                    navController.popBackStack(hu.bme.aut.android.examapp.service.navigation.ExamDestination.LoginScreenDestination.route, inclusive = true)
                    navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.LoginScreenDestination.route)
                }
            )
        }

        // Define each list screen's composable
        composable(route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.TopicListDestination.route) {
            TopicListScreen(
                addNewTopic = { /* handle adding a new topic */ },
                navigateToTopicDetails = { /* handle navigation to topic details */ },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.PointListDestination.route) {
            PointListScreen(
                addNewPoint = { /* handle adding a new topic */ },
                navigateToPointDetails = { /* handle navigation to topic details */ },
                navigateBack = { navController.popBackStack() }
            )
        }

        // Add other list screens similarly
    }
}