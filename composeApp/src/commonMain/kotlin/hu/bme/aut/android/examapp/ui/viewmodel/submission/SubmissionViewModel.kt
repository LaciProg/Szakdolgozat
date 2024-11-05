package hu.bme.aut.android.examapp.ui.viewmodel.submission

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.android.examapp.Type
import hu.bme.aut.android.examapp.api.dto.ExamDto
import hu.bme.aut.android.examapp.api.dto.MultipleChoiceQuestionDto
import hu.bme.aut.android.examapp.api.dto.Question
import hu.bme.aut.android.examapp.api.dto.StatisticsDto
import hu.bme.aut.android.examapp.api.dto.TrueFalseQuestionDto
import hu.bme.aut.android.examapp.navigation.ExamDestination
import hu.bme.aut.android.examapp.ui.viewmodel.exam.ExamDetailsUiState
import hu.bme.aut.android.examapp.ui.viewmodel.exam.toExamDetails
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface SubmissionScreenUiState {
    data class Success(val examDto: ExamDto) : SubmissionScreenUiState
    data object Error : SubmissionScreenUiState {var errorMessage: String = ""}
    data object Loading : SubmissionScreenUiState
    data object Camera : SubmissionScreenUiState

}

sealed interface SubmissionResultScreenUiState {
    data class Success(val statisticsDto: StatisticsDto) : SubmissionResultScreenUiState
    data object Error : SubmissionResultScreenUiState {var errorMessage: String = ""}
    data object Loading : SubmissionResultScreenUiState
}

class SubmissionViewModel(
    val examId: String,
) : ViewModel() {

    var submissionScreenUiState: SubmissionScreenUiState by mutableStateOf(SubmissionScreenUiState.Loading)
    var statisticsDialogUiState: SubmissionResultScreenUiState by mutableStateOf(SubmissionResultScreenUiState.Loading)

    var uiState by mutableStateOf(ExamDetailsUiState())

    val answers = Answers(mutableListOf())

    var statisticsDto by mutableStateOf(null as StatisticsDto?)

    init {
        getExam(examId)
    }


    fun getExam(topicId: String, gottenAnswers: Answers = this.answers){
        submissionScreenUiState = SubmissionScreenUiState.Loading
        viewModelScope.launch {
            submissionScreenUiState = try{
                val result = ApiService.getExam(topicId)
                uiState = ExamDetailsUiState(result.toExamDetails(
                    topicName =
                    if (result.topicId == "null") ""
                    else ApiService.getTopic(result.topicId).topic,
                    questionList = if(result.questionList == "") listOf() else result.questionList.split("#").map { if(it.toQuestion() != null) it.toQuestion()!! else throw IllegalArgumentException("Invalid type")}
                ))
                if(gottenAnswers.answers.isEmpty()) {
                    repeat(uiState.examDetails.questionList.size) {
                        answers.answers.add("")
                    }
                }
                //Log.i("Answers3", gottenAnswers.answers.toString())
                //if(gottenAnswers.answers.isNotEmpty()) {
                //    gottenAnswers.answers.forEachIndexed { index, it ->
                //        Log.i("Answer", it)
                //        answers.answers[index] = it
                //    }
                //}


                SubmissionScreenUiState.Success(result)
            } catch (e: IOException) {
                SubmissionScreenUiState.Error
            } /*catch (e: HttpException) {
                when(e.code()){
                    400 -> SubmissionScreenUiState.Error.errorMessage = "Bad request"
                    401 -> SubmissionScreenUiState.Error.errorMessage = "Unauthorized try logging in again or open the home screen"
                    404 -> SubmissionScreenUiState.Error.errorMessage = "Content not found"
                    500 -> SubmissionScreenUiState.Error.errorMessage = "Server error"
                    else -> SubmissionScreenUiState.Error
                }
                SubmissionScreenUiState.Error
            }*/ catch (e: IllegalArgumentException){
                SubmissionScreenUiState.Error.errorMessage = "Server type"
                SubmissionScreenUiState.Error
            }
        }
    }

    fun submitAnswers(answers: String): StatisticsDto?{
        statisticsDialogUiState = SubmissionResultScreenUiState.Loading
        viewModelScope.launch {
            try {
                statisticsDto = ApiService.getCorrection(id = examId, answers = answers)
                statisticsDialogUiState = SubmissionResultScreenUiState.Success(statisticsDto!!)
            } catch (e: IOException) {
                SubmissionScreenUiState.Error.errorMessage = "Network error"
                statisticsDialogUiState = SubmissionResultScreenUiState.Error
            } /*catch (e: HttpException) {
                when(e.code()){
                    400 -> SubmissionResultScreenUiState.Error.errorMessage = "Bad request"
                    401 -> SubmissionResultScreenUiState.Error.errorMessage = "Unauthorized try logging in again or open the home screen"
                    404 -> SubmissionResultScreenUiState.Error.errorMessage = "Content not found"
                    412 -> SubmissionResultScreenUiState.Error.errorMessage = "Wrong answers format"
                    500 -> SubmissionResultScreenUiState.Error.errorMessage = "Server error"
                    else -> SubmissionResultScreenUiState.Error
                }
                statisticsDialogUiState = SubmissionResultScreenUiState.Error
            }*/
        }
        return statisticsDto
    }

    private suspend fun String.toQuestion(): Question? {
        val question = this.split("~")
        val type = question[0].toInt()
        val questionId = question[1]
        return when(type){
            Type.trueFalseQuestion.ordinal -> toTrueFalseQuestion(questionId)
            Type.multipleChoiceQuestion.ordinal -> toMultipleChoiceQuestion(questionId)
            else -> throw IllegalArgumentException("Invalid type")
        }

    }

    private suspend fun toTrueFalseQuestion(id: String) : TrueFalseQuestionDto? {
        return try {
            ApiService.getTrueFalse(id)
        } catch (e: IOException) {
            SubmissionScreenUiState.Error.errorMessage = "Network error"
            submissionScreenUiState = SubmissionScreenUiState.Error
            null
        } /*catch (e: HttpException) {
            when(e.code()){
                400 -> SubmissionScreenUiState.Error.errorMessage = "Bad request"
                401 -> SubmissionScreenUiState.Error.errorMessage = "Unauthorized try logging in again or open the home screen"
                404 -> SubmissionScreenUiState.Error.errorMessage = "Content not found"
                500 -> SubmissionScreenUiState.Error.errorMessage = "Server error"
                else -> SubmissionScreenUiState.Error
            }
            submissionScreenUiState = SubmissionScreenUiState.Error
            null
        }*/
    }

    private suspend fun toMultipleChoiceQuestion(id: String) : MultipleChoiceQuestionDto? {
        return try {
            ApiService.getMultipleChoice(id)
        } catch (e: IOException) {
            SubmissionScreenUiState.Error.errorMessage = "Network error"
            submissionScreenUiState = SubmissionScreenUiState.Error
            null
        } /*catch (e: HttpException) {
            when(e.code()){
                400 -> SubmissionScreenUiState.Error.errorMessage = "Bad request"
                401 -> SubmissionScreenUiState.Error.errorMessage = "Unauthorized try logging in again or open the home screen"
                404 -> SubmissionScreenUiState.Error.errorMessage = "Content not found"
                500 -> SubmissionScreenUiState.Error.errorMessage = "Server error"
                else -> SubmissionScreenUiState.Error
            }
            submissionScreenUiState = SubmissionScreenUiState.Error
            null
        }*/
    }

}

data class Answers(
    val answers: MutableList<String>
)