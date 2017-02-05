package yobu.christophpickl.github.com.yobu

import org.json.JSONObject
import java.io.InputStream


data class JsonQuestion(val text: String)

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
        val questions = json.getJSONArray("questions_catalog")
        LOG.d { "Read JSON: $json" }

        return JsonCatalog(
                1.rangeTo(questions.length()).map {
                    toJsonQuestion(questions.getJSONObject(it - 1))
                })
    }

    private fun toJsonQuestion(json: JSONObject): JsonQuestion {
        return JsonQuestion(
                json.getString("text")
        )
    }
}
