package hu.bme.aut.android.examapp.service.navigation

sealed class ExamDestination(val route: String) {
    data object LoginScreenDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("LoginScreen")

    data object RegisterScreenDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("RegisterScreen")

    data object MainScreenDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("MainScreen")

    data object TopicListDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("TopicList")

    data object TopicDetailsDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("TopicDetails") {
        const val topicIdArg = "0"
        val routeWithArgs = "$route/{${hu.bme.aut.android.examapp.service.navigation.ExamDestination.TopicDetailsDestination.topicIdArg}}"
    }

    data object TopicEditDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("TopicEdit") {
        const val topicIdArg = "0"
        val routeWithArgs = "$route/{${hu.bme.aut.android.examapp.service.navigation.ExamDestination.TopicEditDestination.topicIdArg}}"
    }

    data object NewTopicDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("NewTopic")

    data object PointListDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("PointList")

    data object PointDetailsDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("PointDetails") {
        const val pointIdArg = "0"
        val routeWithArgs = "$route/{${hu.bme.aut.android.examapp.service.navigation.ExamDestination.PointDetailsDestination.pointIdArg}}"
    }

    data object PointEditDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("PointEdit") {
        const val pointIdArg = "0"
        val routeWithArgs = "$route/{${hu.bme.aut.android.examapp.service.navigation.ExamDestination.PointEditDestination.pointIdArg}}"
    }

    data object NewPointDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("NewPoint")

    data object TrueFalseQuestionListDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("TrueFalseQuestionList")

    data object TrueFalseQuestionDetailsDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("TrueFalseQuestionDetails") {
        const val trueFalseQuestionIdArg = "0"
        val routeWithArgs = "$route/{${hu.bme.aut.android.examapp.service.navigation.ExamDestination.TrueFalseQuestionDetailsDestination.trueFalseQuestionIdArg}}"
    }

    data object TrueFalseQuestionEditDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("TrueFalseQuestionEdit") {
        const val trueFalseQuestionIdArg = "0"
        val routeWithArgs = "$route/{${hu.bme.aut.android.examapp.service.navigation.ExamDestination.TrueFalseQuestionEditDestination.trueFalseQuestionIdArg}}"
    }

    data object NewTrueFalseQuestionDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("NewTrueFalseQuestion")

    data object MultipleChoiceQuestionListDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("MultipleChoiceQuestionList")

    data object MultipleChoiceQuestionDetailsDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("MultipleChoiceQuestionDetails") {
        const val multipleChoiceQuestionIdArg = "0"
        val routeWithArgs = "$route/{${hu.bme.aut.android.examapp.service.navigation.ExamDestination.MultipleChoiceQuestionDetailsDestination.multipleChoiceQuestionIdArg}}"
    }

    data object MultipleChoiceQuestionEditDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("MultipleChoiceQuestionEdit") {
        const val multipleChoiceQuestionIdArg = "0"
        val routeWithArgs = "$route/{${hu.bme.aut.android.examapp.service.navigation.ExamDestination.MultipleChoiceQuestionEditDestination.multipleChoiceQuestionIdArg}}"
    }

    data object NewMultipleChoiceQuestionDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("NewMultipleChoiceQuestion")


    data object ExamListDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("ExamList")

    data object ExamDetailsDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("ExamDetails") {
        const val examIdArg = "0"
        val routeWithArgs = "$route/{${hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExamDetailsDestination.examIdArg}}"
    }

    data object ExamEditDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("ExamEdit") {
        const val examIdArg = "0"
        val routeWithArgs = "$route/{${hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExamEditDestination.examIdArg}}"
    }

    data object NewExamDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("NewExam")

    data object SubmissionListDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("SubmissionLis")

    data object SubmissionDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("Submission") {
        const val examIdArg = "0"
        val routeWithArgs = "$route/{${hu.bme.aut.android.examapp.service.navigation.ExamDestination.SubmissionDestination.examIdArg}}"
    }

    data object ExportExamListDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("ExportExamList")

    data object ExportExamDetailsDestination : hu.bme.aut.android.examapp.service.navigation.ExamDestination("ExportExamDetails") {
        const val examIdArg = "0"
        val routeWithArgs = "$route/{${hu.bme.aut.android.examapp.service.navigation.ExamDestination.ExportExamDetailsDestination.examIdArg}}"
    }
}