package hu.bme.aut.android.examapp.ui.viewmodel.submission

import ApiException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.bme.aut.android.examapp.service.api.dto.*
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
    var examId: String,
) : ViewModel() {

    var submissionScreenUiState: SubmissionScreenUiState by mutableStateOf(SubmissionScreenUiState.Loading)
    var statisticsDialogUiState: SubmissionResultScreenUiState by mutableStateOf(SubmissionResultScreenUiState.Loading)

    var uiState by mutableStateOf(ExamDetailsUiState())

    val answers = Answers(mutableListOf())

    var statisticsDto by mutableStateOf(null as StatisticsDto?)

    init {
        getExam(examId)
    }

    fun setId(id: String){
        examId = id
        getExam(id)
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
                SubmissionScreenUiState.Success(result)
            } catch (e: IOException) {
                SubmissionScreenUiState.Error.errorMessage = "IO error"
                SubmissionScreenUiState.Error
            }catch (e: ApiException) {
                SubmissionScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                SubmissionScreenUiState.Error
            } catch (e: Exception){
                SubmissionScreenUiState.Error.errorMessage = "Network error"
                SubmissionScreenUiState.Error
            }
        }
    }

    fun submitAnswers(answers: String): StatisticsDto?{
        statisticsDialogUiState = SubmissionResultScreenUiState.Loading
        viewModelScope.launch {
            statisticsDialogUiState = try {
                statisticsDto = ApiService.getCorrection(id = examId, answers = answers)
                SubmissionResultScreenUiState.Success(statisticsDto!!)
            } catch (e: IOException) {
                SubmissionResultScreenUiState.Error.errorMessage = "Network error"
                SubmissionResultScreenUiState.Error
            } catch (e: IOException) {
                SubmissionResultScreenUiState.Error.errorMessage = "IO error"
                SubmissionResultScreenUiState.Error
            }catch (e: ApiException) {
                SubmissionResultScreenUiState.Error.errorMessage = e.message?: "Unkown error"
                SubmissionResultScreenUiState.Error
            } catch (e: Exception){
                SubmissionResultScreenUiState.Error.errorMessage = "Network error"
                SubmissionResultScreenUiState.Error
            }
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
        } catch (e: IOException) {
            SubmissionScreenUiState.Error.errorMessage = "Network error"
            submissionScreenUiState = SubmissionScreenUiState.Error
            null
        } catch (e: IOException) {
            SubmissionScreenUiState.Error.errorMessage = "IO error"
            submissionScreenUiState = SubmissionScreenUiState.Error
            null
        }catch (e: ApiException) {
            SubmissionScreenUiState.Error.errorMessage = e.message?: "Unkown error"
            submissionScreenUiState = SubmissionScreenUiState.Error
            null
        } catch (e: Exception){
            SubmissionScreenUiState.Error.errorMessage = "Network error"
            submissionScreenUiState = SubmissionScreenUiState.Error
            null
        }

    }

    private suspend fun toMultipleChoiceQuestion(id: String) : MultipleChoiceQuestionDto? {
        return try {
            ApiService.getMultipleChoice(id)
        } catch (e: IOException) {
            SubmissionScreenUiState.Error.errorMessage = "Network error"
            submissionScreenUiState = SubmissionScreenUiState.Error
            null
        } catch (e: IOException) {
                SubmissionScreenUiState.Error.errorMessage = "Network error"
                submissionScreenUiState = SubmissionScreenUiState.Error
                null
        } catch (e: IOException) {
            SubmissionScreenUiState.Error.errorMessage = "IO error"
            submissionScreenUiState = SubmissionScreenUiState.Error
            null
        }catch (e: ApiException) {
            SubmissionScreenUiState.Error.errorMessage = e.message?: "Unkown error"
            submissionScreenUiState = SubmissionScreenUiState.Error
            null
        } catch (e: Exception){
            SubmissionScreenUiState.Error.errorMessage = "Network error"
            submissionScreenUiState = SubmissionScreenUiState.Error
            null
        }
    }

}

data class Answers(
    val answers: MutableList<String>
)