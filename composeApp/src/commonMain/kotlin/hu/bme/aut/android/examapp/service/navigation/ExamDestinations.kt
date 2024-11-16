package hu.bme.aut.android.examapp.navigation

sealed class ExamDestination(val route: String) {
    data object LoginScreenDestination : ExamDestination("LoginScreen")

    data object RegisterScreenDestination : ExamDestination("RegisterScreen")

    data object MainScreenDestination : ExamDestination("MainScreen")

    data object TopicListDestination : ExamDestination("TopicList")

    data object TopicDetailsDestination : ExamDestination("TopicDetails") {
        const val topicIdArg = "0"
        val routeWithArgs = "$route/{$topicIdArg}"
    }

    data object TopicEditDestination : ExamDestination("TopicEdit") {
        const val topicIdArg = "0"
        val routeWithArgs = "$route/{$topicIdArg}"
    }

    data object NewTopicDestination : ExamDestination("NewTopic")

    data object PointListDestination : ExamDestination("PointList")

    data object PointDetailsDestination : ExamDestination("PointDetails") {
        const val pointIdArg = "0"
        val routeWithArgs = "$route/{$pointIdArg}"
    }

    data object PointEditDestination : ExamDestination("PointEdit") {
        const val pointIdArg = "0"
        val routeWithArgs = "$route/{$pointIdArg}"
    }

    data object NewPointDestination : ExamDestination("NewPoint")

    data object TrueFalseQuestionListDestination : ExamDestination("TrueFalseQuestionList")

    data object TrueFalseQuestionDetailsDestination : ExamDestination("TrueFalseQuestionDetails") {
        const val trueFalseQuestionIdArg = "0"
        val routeWithArgs = "$route/{$trueFalseQuestionIdArg}"
    }

    data object TrueFalseQuestionEditDestination : ExamDestination("TrueFalseQuestionEdit") {
        const val trueFalseQuestionIdArg = "0"
        val routeWithArgs = "$route/{$trueFalseQuestionIdArg}"
    }

    data object NewTrueFalseQuestionDestination : ExamDestination("NewTrueFalseQuestion")

    data object MultipleChoiceQuestionListDestination : ExamDestination("MultipleChoiceQuestionList")

    data object MultipleChoiceQuestionDetailsDestination : ExamDestination("MultipleChoiceQuestionDetails") {
        const val multipleChoiceQuestionIdArg = "0"
        val routeWithArgs = "$route/{$multipleChoiceQuestionIdArg}"
    }

    data object MultipleChoiceQuestionEditDestination : ExamDestination("MultipleChoiceQuestionEdit") {
        const val multipleChoiceQuestionIdArg = "0"
        val routeWithArgs = "$route/{$multipleChoiceQuestionIdArg}"
    }

    data object NewMultipleChoiceQuestionDestination : ExamDestination("NewMultipleChoiceQuestion")


    data object ExamListDestination : ExamDestination("ExamList")

    data object ExamDetailsDestination : ExamDestination("ExamDetails") {
        const val examIdArg = "0"
        val routeWithArgs = "$route/{$examIdArg}"
    }

    data object ExamEditDestination : ExamDestination("ExamEdit") {
        const val examIdArg = "0"
        val routeWithArgs = "$route/{$examIdArg}"
    }

    data object NewExamDestination : ExamDestination("NewExam")

    data object SubmissionListDestination : ExamDestination("SubmissionLis")

    data object SubmissionDestination : ExamDestination("Submission") {
        const val examIdArg = "0"
        val routeWithArgs = "$route/{$examIdArg}"
    }

    data object ExportExamListDestination : ExamDestination("ExportExamList")

    data object ExportExamDetailsDestination : ExamDestination("ExportExamDetails") {
        const val examIdArg = "0"
        val routeWithArgs = "$route/{$examIdArg}"
    }
}