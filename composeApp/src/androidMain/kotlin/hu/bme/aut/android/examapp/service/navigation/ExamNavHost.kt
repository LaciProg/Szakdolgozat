package hu.bme.aut.android.examapp.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import hu.bme.aut.android.examapp.service.pdf.ExportExamDetailsScreen
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
        startDestination = hu.bme.aut.android.examapp.service.navigation.ExamDestination.MainScreenDestination.route,
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
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.MainScreenDestination.route
        ){
        MainScreen(
                navigateToTopicList = { navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.TopicListDestination.route) },
                navigateToPointList = { navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.PointListDestination.route) },
                navigateToTrueFalseQuestionList = { navController.navigate(
                    hu.bme.aut.android.examapp.service.navigation.ExamDestination.TrueFalseQuestionListDestination.route) },
                navigateToMultipleChoiceQuestionList = {navController.navigate(
                    hu.bme.aut.android.examapp.service.navigation.ExamDestination.MultipleChoiceQuestionListDestination.route)},
                navigateToExamList = {navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExamListDestination.route)},
                navigateToExportExamList = {navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExportExamListDestination.route)},
                navigateToSubmission = {navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.SubmissionListDestination.route)},
                onSignOut = {
                    navController.popBackStack(
                        route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.LoginScreenDestination.route,
                        inclusive = true
                    )
                    navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.LoginScreenDestination.route)
                }
            )
        }

        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.TopicListDestination.route,
        ) {
            TopicListScreen(
                addNewTopic = { navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.NewTopicDestination.route) },
                navigateToTopicDetails = { topicId ->
                    navController.navigate("${hu.bme.aut.android.examapp.service.navigation.ExamDestination.TopicDetailsDestination.route}/${topicId}")
                },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.TopicDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(hu.bme.aut.android.examapp.service.navigation.ExamDestination.TopicDetailsDestination.topicIdArg) {
                type = NavType.StringType
            })
        ) {
            val id = it.arguments?.getString(hu.bme.aut.android.examapp.service.navigation.ExamDestination.TopicDetailsDestination.topicIdArg) ?: "0"
            TopicDetailsScreen(
                navigateToEditTopic = { navController.navigate("${hu.bme.aut.android.examapp.service.navigation.ExamDestination.TopicEditDestination.route}/$id") },
                topicId = id,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.TopicEditDestination.routeWithArgs,
            arguments = listOf(navArgument(hu.bme.aut.android.examapp.service.navigation.ExamDestination.TopicEditDestination.topicIdArg) {
                type = NavType.StringType
            })
        ) {
            val id = it.arguments?.getString(hu.bme.aut.android.examapp.service.navigation.ExamDestination.TopicEditDestination.topicIdArg) ?: "0"
            TopicEditScreen(
                navigateBack = { navController.popBackStack() },
                topicId = id,
            )
        }

        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.NewTopicDestination.route
        ) {
            NewTopic(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }

        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.PointListDestination.route,
        ) {
            PointListScreen(
                addNewPoint = { navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.NewPointDestination.route) },
                navigateToPointDetails = { pointId ->
                    navController.navigate("${hu.bme.aut.android.examapp.service.navigation.ExamDestination.PointDetailsDestination.route}/$pointId")
                },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.PointDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(hu.bme.aut.android.examapp.service.navigation.ExamDestination.PointDetailsDestination.pointIdArg) {
                type = NavType.StringType
            })
        ) {
            val id = it.arguments?.getString(hu.bme.aut.android.examapp.service.navigation.ExamDestination.PointDetailsDestination.pointIdArg) ?: "0"
            PointDetailsScreen(
                navigateToEditPoint = { navController.navigate("${hu.bme.aut.android.examapp.service.navigation.ExamDestination.PointEditDestination.route}/$id") },
                pointId = id,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.PointEditDestination.routeWithArgs,
            arguments = listOf(navArgument(hu.bme.aut.android.examapp.service.navigation.ExamDestination.PointEditDestination.pointIdArg) {
                type = NavType.StringType
            })
        ) {
            val id = it.arguments?.getString(hu.bme.aut.android.examapp.service.navigation.ExamDestination.PointEditDestination.pointIdArg) ?: "0"
            PointEditScreen(
                navigateBack = { navController.popBackStack() },
                pointId = id,
            )
        }

        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.NewPointDestination.route
        ) {
            NewPoint(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }


        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.TrueFalseQuestionListDestination.route
        ) {
            TrueFalseQuestionListScreen(
                addNewTrueFalseQuestion = { navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.NewTrueFalseQuestionDestination.route) },
                navigateToTrueFalseQuestionDetails = { navController.navigate("${hu.bme.aut.android.examapp.service.navigation.ExamDestination.TrueFalseQuestionDetailsDestination.route}/$it") },
                navigateBack = { navController.popBackStack() }
            )
        }


        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.TrueFalseQuestionDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(hu.bme.aut.android.examapp.service.navigation.ExamDestination.TrueFalseQuestionDetailsDestination.trueFalseQuestionIdArg) {
                type = NavType.StringType
            })
        ){
            val id = it.arguments?.getString(hu.bme.aut.android.examapp.service.navigation.ExamDestination.TrueFalseQuestionDetailsDestination.trueFalseQuestionIdArg) ?: "0"
            TrueFalseQuestionDetailsScreen(
                navigateToEditTrueFalseQuestion = {navController.navigate("${hu.bme.aut.android.examapp.service.navigation.ExamDestination.TrueFalseQuestionEditDestination.route}/$it") },
                trueFalseQuestionId = id,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.TrueFalseQuestionEditDestination.routeWithArgs,
            arguments = listOf(navArgument(hu.bme.aut.android.examapp.service.navigation.ExamDestination.TrueFalseQuestionEditDestination.trueFalseQuestionIdArg) {
                type = NavType.StringType
            })
        ) {
            val id = it.arguments?.getString(hu.bme.aut.android.examapp.service.navigation.ExamDestination.TrueFalseQuestionDetailsDestination.trueFalseQuestionIdArg) ?: "0"
            TrueFalseQuestionEditScreen(
                navigateBack = { navController.popBackStack() },
                trueFalseQuestionId = id,
            )
        }


        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.NewTrueFalseQuestionDestination.route
        ) {
            NewTrueFalseQuestionScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }


        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.MultipleChoiceQuestionListDestination.route
        ) {
            MultipleChoiceQuestionListScreen(
                addNewMultipleChoiceQuestion = { navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.NewMultipleChoiceQuestionDestination.route) },
                navigateToMultipleChoiceQuestionDetails = { navController.navigate("${hu.bme.aut.android.examapp.service.navigation.ExamDestination.MultipleChoiceQuestionDetailsDestination.route}/$it") },
                navigateBack = { navController.popBackStack() }
            )
        }


        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.MultipleChoiceQuestionDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(hu.bme.aut.android.examapp.service.navigation.ExamDestination.MultipleChoiceQuestionDetailsDestination.multipleChoiceQuestionIdArg) {
                type = NavType.StringType
            })
        ){
            val id = it.arguments?.getString(hu.bme.aut.android.examapp.service.navigation.ExamDestination.MultipleChoiceQuestionDetailsDestination.multipleChoiceQuestionIdArg) ?: "0"
            MultipleChoiceQuestionDetailsScreen(
                navigateToEditMultipleChoiceQuestion = {navController.navigate("${ hu.bme.aut.android.examapp.service.navigation.ExamDestination.MultipleChoiceQuestionEditDestination.route}/$it") },
                multipleChoiceQuestionId = id,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route =  hu.bme.aut.android.examapp.service.navigation.ExamDestination.MultipleChoiceQuestionEditDestination.routeWithArgs,
            arguments = listOf(navArgument(hu.bme.aut.android.examapp.service.navigation.ExamDestination.MultipleChoiceQuestionEditDestination.multipleChoiceQuestionIdArg) {
                type = NavType.StringType
            })
        ) {
            val id = it.arguments?.getString(hu.bme.aut.android.examapp.service.navigation.ExamDestination.MultipleChoiceQuestionEditDestination.multipleChoiceQuestionIdArg) ?: "0"
            MultipleChoiceQuestionEditScreen(
                navigateBack = { navController.popBackStack() },
                multipleChoiceQuestionId = id,
            )
        }


        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.NewMultipleChoiceQuestionDestination.route
        ) {
            NewMultipleChoiceQuestionScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }





        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExamListDestination.route
        ) {
            ExamListScreen(
                addNewExam = { navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.NewExamDestination.route) },
                navigateToExamDetails = { navController.navigate("${ hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExamDetailsDestination.route}/$it") },
                navigateBack = { navController.popBackStack() }
            )
        }


        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExamDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExamDetailsDestination.examIdArg) {
                type = NavType.StringType
            })
        ){
            val id = it.arguments?.getString(hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExamDetailsDestination.examIdArg) ?: "0"
            ExamDetailsScreen(
                navigateToEditMultipleChoiceQuestion = {navController.navigate("${ hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExamEditDestination.route}/$it") },
                examId = id,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route =  hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExamEditDestination.routeWithArgs,
            arguments = listOf(navArgument(hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExamEditDestination.examIdArg) {
                type = NavType.StringType
            })
        ) {
            val id = it.arguments?.getString(hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExamEditDestination.examIdArg) ?: "0"
            ExamEditScreen(
                navigateBack = { navController.popBackStack() },
                examId = id,
            )
        }


        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.NewExamDestination.route
        ) {
            NewExamScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }

        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.SubmissionListDestination.route
        ) {
            ExamListScreen(
                addNewExam = { navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.NewExamDestination.route) },
                navigateToExamDetails = { navController.navigate("${hu.bme.aut.android.examapp.service.navigation.ExamDestination.SubmissionDestination.route}/$it") },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.SubmissionDestination.routeWithArgs,
            arguments = listOf(navArgument(hu.bme.aut.android.examapp.service.navigation.ExamDestination.SubmissionDestination.examIdArg) {
                type = NavType.StringType
            })
        ) {
            val id = it.arguments?.getString(hu.bme.aut.android.examapp.service.navigation.ExamDestination.SubmissionDestination.examIdArg) ?: "0"
            SubmissionScreen(
                navigateBack = { navController.popBackStack() },
                navigateBackCamera = { navController.popBackStack() },
                examId = id,
            )
        }


        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExportExamListDestination.route
        ) {
            ExamListScreen(
                addNewExam = { navController.navigate(hu.bme.aut.android.examapp.service.navigation.ExamDestination.NewExamDestination.route) },
                navigateToExamDetails = { navController.navigate("${hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExportExamDetailsDestination.route}/$it") },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExportExamDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExportExamDetailsDestination.examIdArg) {
                type = NavType.StringType
            })
        ){
            val id = it.arguments?.getString(hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExportExamDetailsDestination.examIdArg) ?: "0"
            hu.bme.aut.android.examapp.service.pdf.ExportExamDetailsScreen(
                //navigateToEditMultipleChoiceQuestion = {navController.navigate("${ ExamEditDestination.route}/$it") },
                navigateBack = { navController.popBackStack() },
                examId = id,
            )
        }
    }
}