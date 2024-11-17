import hu.bme.aut.android.examapp.service.api.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ApiException(message: String) : Exception(message)

private fun handleHttpException(code: HttpStatusCode): String {
    return when (code) {
        HttpStatusCode.BadRequest -> "Bad request"
        HttpStatusCode.Unauthorized -> "Unauthorized. Try logging in again or open the home screen."
        HttpStatusCode.NotFound -> "Content not found"
        HttpStatusCode.PreconditionFailed -> "Wrong answer format"
        HttpStatusCode.InternalServerError -> "Server error"
        else -> "Unknown error occurred. Please try again later."
    }
}

object ApiService {
    private var authToken: String? = null  // Mutable token that can be updated at runtime

    private val httpClient = HttpClient() {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }

        defaultRequest {
            url("http://mlaci.sch.bme.hu:46258")  // Set the base URL   152.66.182.70:46258   192.168.1.17:46258
            authToken?.let { token ->
                header(HttpHeaders.Authorization, "Bearer $token")  // Add the Bearer token if it's not null
            }
        }
    }

    // Function to update the token at runtime
    fun updateToken(newToken: String) {
        authToken = newToken
    }

    // Authentication API végpont
    suspend fun authenticate(user: UserDto): Token {
        val response = httpClient.post("/auth") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    // User API végpontok
    suspend fun getAllUsers(): List<UserDto> {
        val response = httpClient.get("/user")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun getUser(id: String): UserDto {
        val response = httpClient.get("/user/$id")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun getUserByName(userName: String): UserDto? {
        val response = httpClient.get("/user/name/$userName")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun postUser(user: UserDto): UserDto? {
        val response = httpClient.post("/user") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
        if (response.status == HttpStatusCode.Created) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun updateUser(user: UserDto): HttpResponse {
        val response = httpClient.put("/user") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
        if (response.status == HttpStatusCode.OK) return response
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun deleteUser(id: String): HttpResponse {
        val response = httpClient.delete("/user/$id")
        if (response.status == HttpStatusCode.NoContent) return response
        throw ApiException(handleHttpException(response.status))
    }

    // Point API végpontok
    suspend fun getAllPoints(): List<PointDto> {
        val response = httpClient.get("/point")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun getAllPointNames(): List<NameDto> {
        val response = httpClient.get("/point/name")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun getPoint(id: String): PointDto {
        val response = httpClient.get("/point/$id")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun deletePoint(id: String): HttpResponse {
        val response = httpClient.delete("/point/$id")
        if (response.status == HttpStatusCode.NoContent) return response
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun postPoint(point: PointDto): PointDto? {
        val response = httpClient.post("/point") {
            contentType(ContentType.Application.Json)
            setBody(point)
        }
        if (response.status == HttpStatusCode.Created) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun updatePoint(point: PointDto): HttpResponse {
        val response = httpClient.put("/point") {
            contentType(ContentType.Application.Json)
            setBody(point)
        }
        if (response.status == HttpStatusCode.OK) return response
        throw ApiException(handleHttpException(response.status))
    }

    // Topic API végpontok
    suspend fun getAllTopics(): List<TopicDto> {
        val response = httpClient.get("/topic")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun getAllTopicNames(): List<NameDto> {
        val response = httpClient.get("/topic/name")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun getTopic(id: String): TopicDto {
        val response = httpClient.get("/topic/$id")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun getTopicByTopic(topic: String): TopicDto? {
        val response = httpClient.get("/topic/name/$topic")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun deleteTopic(id: String): HttpResponse {
        val response = httpClient.delete("/topic/$id")
        if (response.status == HttpStatusCode.NoContent) return response
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun postTopic(topic: TopicDto): TopicDto? {
        val response = httpClient.post("/topic") {
            contentType(ContentType.Application.Json)
            setBody(topic)
        }
        if (response.status == HttpStatusCode.Created) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun updateTopic(topic: TopicDto): HttpResponse {
        val response = httpClient.put("/topic") {
            contentType(ContentType.Application.Json)
            setBody(topic)
        }
        if (response.status == HttpStatusCode.OK) return response
        throw ApiException(handleHttpException(response.status))
    }

    // TrueFalse API végpontok
    suspend fun getAllTrueFalse(): List<TrueFalseQuestionDto> {
        val response = httpClient.get("/trueFalse")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun getAllTrueFalseNames(): List<NameDto> {
        val response = httpClient.get("/trueFalse/name")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun getTrueFalse(id: String): TrueFalseQuestionDto {
        val response = httpClient.get("/trueFalse/$id")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun deleteTrueFalse(id: String): HttpResponse {
        val response = httpClient.delete("/trueFalse/$id")
        if (response.status == HttpStatusCode.NoContent) return response
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun postTrueFalse(trueFalseQuestion: TrueFalseQuestionDto): TrueFalseQuestionDto? {
        val response = httpClient.post("/trueFalse") {
            contentType(ContentType.Application.Json)
            setBody(trueFalseQuestion)
        }
        if (response.status == HttpStatusCode.Created) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun updateTrueFalse(trueFalseQuestion: TrueFalseQuestionDto): HttpResponse {
        val response = httpClient.put("/trueFalse") {
            contentType(ContentType.Application.Json)
            setBody(trueFalseQuestion)
        }
        if (response.status == HttpStatusCode.OK) return response
        throw ApiException(handleHttpException(response.status))
    }

    // MultipleChoice API végpontok
    suspend fun getAllMultipleChoice(): List<MultipleChoiceQuestionDto> {
        val response = httpClient.get("/multipleChoice")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun getAllMultipleChoiceNames(): List<NameDto> {
        val response = httpClient.get("/multipleChoice/name")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun getMultipleChoice(id: String): MultipleChoiceQuestionDto {
        val response = httpClient.get("/multipleChoice/$id")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun deleteMultipleChoice(id: String): HttpResponse {
        val response = httpClient.delete("/multipleChoice/$id")
        if (response.status == HttpStatusCode.NoContent) return response
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun postMultipleChoice(mcQuestion: MultipleChoiceQuestionDto): MultipleChoiceQuestionDto? {
        val response = httpClient.post("/multipleChoice") {
            contentType(ContentType.Application.Json)
            setBody(mcQuestion)
        }
        if (response.status == HttpStatusCode.Created) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun updateMultipleChoice(mcQuestion: MultipleChoiceQuestionDto): HttpResponse {
        val response = httpClient.put("/multipleChoice") {
            contentType(ContentType.Application.Json)
            setBody(mcQuestion)
        }
        if (response.status == HttpStatusCode.OK) return response
        throw ApiException(handleHttpException(response.status))
    }

    // Exam API végpontok

    suspend fun getAllExams(): List<ExamDto> {
        val response = httpClient.get("/exam")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun getAllExamNames(): List<NameDto> {
        val response = httpClient.get("/exam/name")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun getExam(id: String): ExamDto {
        val response = httpClient.get("/exam/$id")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun getExamQuestions(id: String): ExamDto {
        val response = httpClient.get("/exam/$id/question")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun deleteExam(id: String): HttpResponse {
        val response = httpClient.delete("/exam/$id")
        if (response.status == HttpStatusCode.NoContent) return response
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun postExam(exam: ExamDto): ExamDto? {
        val response = httpClient.post("/exam") {
            contentType(ContentType.Application.Json)
            setBody(exam)
        }
        if (response.status == HttpStatusCode.Created) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun updateExam(exam: ExamDto): HttpResponse {
        val response = httpClient.put("/exam") {
            contentType(ContentType.Application.Json)
            setBody(exam)
        }
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }

    suspend fun getCorrection(id: String, answers: String): StatisticsDto {
        val response = httpClient.get("/correction/$id/$answers")
        if (response.status == HttpStatusCode.OK) return response.body()
        throw ApiException(handleHttpException(response.status))
    }
}
