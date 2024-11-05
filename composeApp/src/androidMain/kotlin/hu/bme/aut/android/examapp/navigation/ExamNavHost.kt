package hu.bme.aut.android.examapp.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import hu.bme.aut.android.examapp.pdf.ExportExamDetailsScreen
import hu.bme.aut.android.examapp.ui.MainScreen
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

@Composable
actual fun ExamNavHost(
    navController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ExamDestination.MainScreenDestination.route,
        modifier = modifier
    ) {

        /*composable(ExamDestination.LoginScreenDestination.route) {
            LoginScreen(
                onSuccess = {
                    navController.navigate(ExamDestination.MainScreenDestination.route)
                },
                onRegisterClick = {
                    navController.navigate(ExamDestination.RegisterScreenDestination.route)
                }
            )
        }*/
        /*composable(ExamDestination.RegisterScreenDestination.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack(
                        route = ExamDestination.LoginScreenDestination.route,
                        inclusive = true
                    )
                    navController.navigate(ExamDestination.LoginScreenDestination.route)
                },
                onSuccess = {
                    navController.navigate(ExamDestination.MainScreenDestination.route)
                }
            )
        }*/


        composable(
            route = ExamDestination.MainScreenDestination.route
        ){
        MainScreen(
                navigateToTopicList = { navController.navigate(ExamDestination.TopicListDestination.route) },
                navigateToPointList = { navController.navigate(ExamDestination.PointListDestination.route) },
                navigateToTrueFalseQuestionList = { navController.navigate(
                    ExamDestination.TrueFalseQuestionListDestination.route) },
                navigateToMultipleChoiceQuestionList = {navController.navigate(
                    ExamDestination.MultipleChoiceQuestionListDestination.route)},
                navigateToExamList = {navController.navigate(ExamDestination.ExamListDestination.route)},
                navigateToExportExamList = {navController.navigate(ExamDestination.ExportExamListDestination.route)},
                navigateToSubmission = {navController.navigate(ExamDestination.SubmissionListDestination.route)},
                onSignOut = {
                    navController.popBackStack(
                        route = ExamDestination.LoginScreenDestination.route,
                        inclusive = true
                    )
                    navController.navigate(ExamDestination.LoginScreenDestination.route)
                }
            )
        }

        composable(
            route = ExamDestination.TopicListDestination.route,
        ) {
            TopicListScreen(
                addNewTopic = { navController.navigate(ExamDestination.NewTopicDestination.route) },
                navigateToTopicDetails = { topicId ->
                    navController.navigate("${ExamDestination.TopicDetailsDestination.route}/${topicId}")
                },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = ExamDestination.TopicDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ExamDestination.TopicDetailsDestination.topicIdArg) {
                type = NavType.StringType
            })
        ) {
            val id = it.arguments?.getString(ExamDestination.TopicDetailsDestination.topicIdArg) ?: "0"
            TopicDetailsScreen(
                navigateToEditTopic = { navController.navigate("${ExamDestination.TopicEditDestination.route}/$id") },
                topicId = id,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = ExamDestination.TopicEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ExamDestination.TopicEditDestination.topicIdArg) {
                type = NavType.StringType
            })
        ) {
            val id = it.arguments?.getString(ExamDestination.TopicEditDestination.topicIdArg) ?: "0"
            TopicEditScreen(
                navigateBack = { navController.popBackStack() },
                topicId = id,
            )
        }

        composable(
            route = ExamDestination.NewTopicDestination.route
        ) {
            NewTopic(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }

        composable(
            route = ExamDestination.PointListDestination.route,
        ) {
            PointListScreen(
                addNewPoint = { navController.navigate(ExamDestination.NewPointDestination.route) },
                navigateToPointDetails = { pointId ->
                    navController.navigate("${ExamDestination.PointDetailsDestination.route}/$pointId")
                },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = ExamDestination.PointDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ExamDestination.PointDetailsDestination.pointIdArg) {
                type = NavType.StringType
            })
        ) {
            val id = it.arguments?.getString(ExamDestination.PointDetailsDestination.pointIdArg) ?: "0"
            PointDetailsScreen(
                navigateToEditPoint = { navController.navigate("${ExamDestination.PointEditDestination.route}/$id") },
                pointId = id,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = ExamDestination.PointEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ExamDestination.PointEditDestination.pointIdArg) {
                type = NavType.StringType
            })
        ) {
            val id = it.arguments?.getString(ExamDestination.PointEditDestination.pointIdArg) ?: "0"
            PointEditScreen(
                navigateBack = { navController.popBackStack() },
                pointId = id,
            )
        }

        composable(
            route = ExamDestination.NewPointDestination.route
        ) {
            NewPoint(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }


        composable(
            route = ExamDestination.TrueFalseQuestionListDestination.route
        ) {
            TrueFalseQuestionListScreen(
                addNewTrueFalseQuestion = { navController.navigate(ExamDestination.NewTrueFalseQuestionDestination.route) },
                navigateToTrueFalseQuestionDetails = { navController.navigate("${ExamDestination.TrueFalseQuestionDetailsDestination.route}/$it") },
                navigateBack = { navController.popBackStack() }
            )
        }


        composable(
            route = ExamDestination.TrueFalseQuestionDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ExamDestination.TrueFalseQuestionDetailsDestination.trueFalseQuestionIdArg) {
                type = NavType.StringType
            })
        ){
            val id = it.arguments?.getString(ExamDestination.TrueFalseQuestionDetailsDestination.trueFalseQuestionIdArg) ?: "0"
            TrueFalseQuestionDetailsScreen(
                navigateToEditTrueFalseQuestion = {navController.navigate("${ExamDestination.TrueFalseQuestionEditDestination.route}/$it") },
                trueFalseQuestionId = id,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = ExamDestination.TrueFalseQuestionEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ExamDestination.TrueFalseQuestionEditDestination.trueFalseQuestionIdArg) {
                type = NavType.StringType
            })
        ) {
            val id = it.arguments?.getString(ExamDestination.TrueFalseQuestionDetailsDestination.trueFalseQuestionIdArg) ?: "0"
            TrueFalseQuestionEditScreen(
                navigateBack = { navController.popBackStack() },
                trueFalseQuestionId = id,
            )
        }


        composable(
            route = ExamDestination.NewTrueFalseQuestionDestination.route
        ) {
            NewTrueFalseQuestionScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }


        composable(
            route = ExamDestination.MultipleChoiceQuestionListDestination.route
        ) {
            MultipleChoiceQuestionListScreen(
                addNewMultipleChoiceQuestion = { navController.navigate(ExamDestination.NewMultipleChoiceQuestionDestination.route) },
                navigateToMultipleChoiceQuestionDetails = { navController.navigate("${ExamDestination.MultipleChoiceQuestionDetailsDestination.route}/$it") },
                navigateBack = { navController.popBackStack() }
            )
        }


        composable(
            route = ExamDestination.MultipleChoiceQuestionDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ExamDestination.MultipleChoiceQuestionDetailsDestination.multipleChoiceQuestionIdArg) {
                type = NavType.StringType
            })
        ){
            val id = it.arguments?.getString(ExamDestination.MultipleChoiceQuestionDetailsDestination.multipleChoiceQuestionIdArg) ?: "0"
            MultipleChoiceQuestionDetailsScreen(
                navigateToEditMultipleChoiceQuestion = {navController.navigate("${ ExamDestination.MultipleChoiceQuestionEditDestination.route}/$it") },
                multipleChoiceQuestionId = id,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route =  ExamDestination.MultipleChoiceQuestionEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ExamDestination.MultipleChoiceQuestionEditDestination.multipleChoiceQuestionIdArg) {
                type = NavType.StringType
            })
        ) {
            val id = it.arguments?.getString(ExamDestination.MultipleChoiceQuestionEditDestination.multipleChoiceQuestionIdArg) ?: "0"
            MultipleChoiceQuestionEditScreen(
                navigateBack = { navController.popBackStack() },
                multipleChoiceQuestionId = id,
            )
        }


        composable(
            route = ExamDestination.NewMultipleChoiceQuestionDestination.route
        ) {
            NewMultipleChoiceQuestionScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }





        composable(
            route = ExamDestination.ExamListDestination.route
        ) {
            ExamListScreen(
                addNewExam = { navController.navigate(ExamDestination.NewExamDestination.route) },
                navigateToExamDetails = { navController.navigate("${ ExamDestination.ExamDetailsDestination.route}/$it") },
                navigateBack = { navController.popBackStack() }
            )
        }


        composable(
            route = ExamDestination.ExamDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ExamDestination.ExamDetailsDestination.examIdArg) {
                type = NavType.StringType
            })
        ){
            val id = it.arguments?.getString(ExamDestination.ExamDetailsDestination.examIdArg) ?: "0"
            ExamDetailsScreen(
                navigateToEditMultipleChoiceQuestion = {navController.navigate("${ ExamDestination.ExamEditDestination.route}/$it") },
                examId = id,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route =  ExamDestination.ExamEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ExamDestination.ExamEditDestination.examIdArg) {
                type = NavType.StringType
            })
        ) {
            val id = it.arguments?.getString(ExamDestination.ExamEditDestination.examIdArg) ?: "0"
            ExamEditScreen(
                navigateBack = { navController.popBackStack() },
                examId = id,
            )
        }


        composable(
            route = ExamDestination.NewExamDestination.route
        ) {
            NewExamScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }

        composable(
            route = ExamDestination.SubmissionListDestination.route
        ) {
            ExamListScreen(
                addNewExam = { navController.navigate(ExamDestination.NewExamDestination.route) },
                navigateToExamDetails = { navController.navigate("${ExamDestination.SubmissionDestination.route}/$it") },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = ExamDestination.SubmissionDestination.routeWithArgs,
            arguments = listOf(navArgument(ExamDestination.SubmissionDestination.examIdArg) {
                type = NavType.StringType
            })
        ) {
            val id = it.arguments?.getString(ExamDestination.SubmissionDestination.examIdArg) ?: "0"
            SubmissionScreen(
                navigateBack = { navController.popBackStack() },
                navigateBackCamera = { navController.popBackStack() },
                examId = id,
            )
        }


        composable(
            route = ExamDestination.ExportExamListDestination.route
        ) {
            ExamListScreen(
                addNewExam = { navController.navigate(ExamDestination.NewExamDestination.route) },
                navigateToExamDetails = { navController.navigate("${ExamDestination.ExportExamDetailsDestination.route}/$it") },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = ExamDestination.ExportExamDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ExamDestination.ExportExamDetailsDestination.examIdArg) {
                type = NavType.StringType
            })
        ){
            val id = it.arguments?.getString(ExamDestination.ExportExamDetailsDestination.examIdArg) ?: "0"
            ExportExamDetailsScreen(
                //navigateToEditMultipleChoiceQuestion = {navController.navigate("${ ExamEditDestination.route}/$it") },
                navigateBack = { navController.popBackStack() },
                examId = id,
            )
        }
    }
}