import hu.bme.aut.android.examapp.api.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

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

    // API Methods
    suspend fun authenticate(user: UserDto): Token = httpClient.post("/auth") {
        contentType(ContentType.Application.Json)
        setBody(user)
    }.body()

    suspend fun getAllUsers(): List<UserDto> = httpClient.get("/user").body()

    suspend fun getUser(id: String): UserDto = httpClient.get("/user/$id").body()

    suspend fun getUserByName(userName: String): UserDto? = httpClient.get("/user/name/$userName").body()

    suspend fun postUser(user: UserDto): UserDto? = httpClient.post("/user") {
        contentType(ContentType.Application.Json)
        setBody(user)
    }.body()

    suspend fun updateUser(user: UserDto): HttpResponse = httpClient.put("/user") {
        contentType(ContentType.Application.Json)
        setBody(user)
    }

    suspend fun deleteUser(id: String): HttpResponse = httpClient.delete("/user/$id")

    suspend fun getAllPoints(): List<PointDto> = httpClient.get("/point").body()

    suspend fun getAllPointNames(): List<NameDto> = httpClient.get("/point/name").body()

    suspend fun getPoint(id: String): PointDto = httpClient.get("/point/$id").body()

    suspend fun deletePoint(id: String): HttpResponse = httpClient.delete("/point/$id")

    suspend fun postPoint(point: PointDto): PointDto? = httpClient.post("/point") {
        contentType(ContentType.Application.Json)
        setBody(point)
    }.body()

    suspend fun updatePoint(point: PointDto): HttpResponse = httpClient.put("/point") {
        contentType(ContentType.Application.Json)
        setBody(point)
    }

    suspend fun getAllTopics(): List<TopicDto> = httpClient.get("/topic").body()

    suspend fun getAllTopicNames(): List<NameDto> = httpClient.get("/topic/name").body()

    suspend fun getTopic(id: String): TopicDto = httpClient.get("/topic/$id").body()

    suspend fun getTopicByTopic(topic: String): TopicDto? = httpClient.get("/topic/name/$topic").body()

    suspend fun deleteTopic(id: String): HttpResponse = httpClient.delete("/topic/$id")

    suspend fun postTopic(topic: TopicDto): TopicDto? = httpClient.post("/topic") {
        contentType(ContentType.Application.Json)
        setBody(topic)
    }.body()

    suspend fun updateTopic(topic: TopicDto): HttpResponse = httpClient.put("/topic") {
        contentType(ContentType.Application.Json)
        setBody(topic)
    }

    suspend fun getAllTrueFalse(): List<TrueFalseQuestionDto> = httpClient.get("/trueFalse").body()

    suspend fun getAllTrueFalseNames(): List<NameDto> = httpClient.get("/trueFalse/name").body()

    suspend fun getTrueFalse(id: String): TrueFalseQuestionDto = httpClient.get("/trueFalse/$id").body()

    suspend fun deleteTrueFalse(id: String): HttpResponse = httpClient.delete("/trueFalse/$id")

    suspend fun postTrueFalse(trueFalseQuestion: TrueFalseQuestionDto): TrueFalseQuestionDto? = httpClient.post("/trueFalse") {
        contentType(ContentType.Application.Json)
        setBody(trueFalseQuestion)
    }.body()

    suspend fun updateTrueFalse(trueFalseQuestion: TrueFalseQuestionDto): HttpResponse = httpClient.put("/trueFalse") {
        contentType(ContentType.Application.Json)
        setBody(trueFalseQuestion)
    }

    suspend fun getAllMultipleChoice(): List<MultipleChoiceQuestionDto> = httpClient.get("/multipleChoice").body()

    suspend fun getAllMultipleChoiceNames(): List<NameDto> = httpClient.get("/multipleChoice/name").body()

    suspend fun getMultipleChoice(id: String): MultipleChoiceQuestionDto = httpClient.get("/multipleChoice/$id").body()

    suspend fun deleteMultipleChoice(id: String): HttpResponse = httpClient.delete("/multipleChoice/$id")

    suspend fun postMultipleChoice(mcQuestion: MultipleChoiceQuestionDto): MultipleChoiceQuestionDto? = httpClient.post("/multipleChoice") {
        contentType(ContentType.Application.Json)
        setBody(mcQuestion)
    }.body()

    suspend fun updateMultipleChoice(mcQuestion: MultipleChoiceQuestionDto): HttpResponse = httpClient.put("/multipleChoice") {
        contentType(ContentType.Application.Json)
        setBody(mcQuestion)
    }

    suspend fun getAllExams(): List<ExamDto> = httpClient.get("/exam").body()

    suspend fun getAllExamNames(): List<NameDto> = httpClient.get("/exam/name").body()

    suspend fun getExam(id: String): ExamDto = httpClient.get("/exam/$id").body()

    suspend fun getExamQuestions(id: String): ExamDto = httpClient.get("/exam/$id/question").body()

    suspend fun deleteExam(id: String): HttpResponse = httpClient.delete("/exam/$id")

    suspend fun postExam(exam: ExamDto): ExamDto? = httpClient.post("/exam") {
        contentType(ContentType.Application.Json)
        setBody(exam)
    }.body()

    suspend fun updateExam(exam: ExamDto): HttpResponse = httpClient.put("/exam") {
        contentType(ContentType.Application.Json)
        setBody(exam)
    }

    suspend fun getCorrection(id: String, answers: String): StatisticsDto = httpClient.get("/correction/$id/$answers").body()
}
