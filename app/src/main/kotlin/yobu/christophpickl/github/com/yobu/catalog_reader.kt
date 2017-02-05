package yobu.christophpickl.github.com.yobu

import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

class CatalogsRepository {
    fun load(questionsStream: InputStream): List<Question> {
        return JsonQuestionReader().read(questionsStream).questions.map { question ->
            Question(question.text, question.answers.mapIndexed { i, answer ->
                Answer(answer.text, isCorrect = i == 0)
            })
        }
    }
}

data class JsonAnswer(val text: String)
data class JsonQuestion(val text: String, val answers: List<JsonAnswer>)

data class JsonCatalog(val questions: List<JsonQuestion>)

class JsonQuestionReader {

    companion object {
        private val LOG = LOG(JsonQuestionReader::class.java)
    }
    // val questionsInputStream = resources.openRawResource(R.raw.questions_catalog)
    // val rawJson = Resources.getSystem().getString(R.raw.questions_catalog)
    fun read(questionsStream: InputStream): JsonCatalog {
        val rawJson = questionsStream.readString()
        val json = JSONObject(rawJson)
        LOG.d { "Read JSON: $json" }

        return JsonCatalog(json.getJSONArray("questions").map {
            toJsonQuestion(it)
        })
    }

    private fun toJsonQuestion(json: JSONObject): JsonQuestion {
        val answers = emptyList<JsonAnswer>()
        return JsonQuestion(
                json.getString("text"),
                json.getJSONArray("answers").map { toJsonAnswer(it) }
        )
    }

    private fun toJsonAnswer(json: JSONObject): JsonAnswer {
        return JsonAnswer(json.getString("text"))
    }
}

fun <T> JSONArray.map(transform: (JSONObject) -> T): List<T> {
    return 1.rangeTo(this.length()).map {
        transform(getJSONObject(it - 1))
    }
}