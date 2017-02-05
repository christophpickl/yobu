package yobu.christophpickl.github.com.yobu.logic

import org.json.JSONArray
import org.json.JSONObject
import yobu.christophpickl.github.com.yobu.Answer
import yobu.christophpickl.github.com.yobu.Question
import yobu.christophpickl.github.com.yobu.misc.readString
import java.io.InputStream


enum class Meridian(val text: String, val points: Int) {
    Lu("Lu", 11),
    Di("Di", 20),
    Ma("Ma", 45),
    MP("MP", 21),
    He("He", 9),
    Due("Due", 19),
    Bl("Bl", 67),
    Ni("Ni", 27),
    Pk("Pk", 9),
    EEE("3E", 23),
    Gb("Gb", 44),
    Le("Le", 14),
//    LG("LG", 28),
    KG("KG", 24)
}

data class PunctCoordinate(val meridian: Meridian, val point: Int) {
    companion object {
        fun parse(string: String): PunctCoordinate {
            val meridian = Meridian.values().firstOrNull { string.startsWith(it.text) } ?: throw IllegalArgumentException("Invalid meridian name: [$string]")
            val number = string.substring(meridian.text.length).toInt()
            if (number < 1 || number > meridian.points) throw IllegalArgumentException("Invalid point number: $number for meridian $meridian")
            return PunctCoordinate(meridian, number)
        }
    }

    fun toNiceString() = meridian.text + point
}

object BoPunctRandomGenerator {
    fun generate(except: PunctCoordinate): PunctCoordinate {
        // TODO change likelyhoods of Bo punct generator
        // * KG=70%
        // * Lu/Ma/Le/Gb restlichen 30%
        // waere cool, wenn zb nach Perikard bo punkt fragt, auch evtl ein rand Pk punkt dabei ist
        val randMeridian = Random.randomOf(Meridian.values(), except.meridian)
        val randPoint = Random.randomBetween(1, randMeridian.points, except.point)
        return PunctCoordinate(randMeridian, randPoint)
    }
}

class CatalogsRepository {
    fun load(questionsStream: InputStream): List<Question> {
        return JsonQuestionReader().read(questionsStream).questions.map { question ->
            Question(question.text, transformAnswers(question))
        }
    }

    private fun transformAnswers(question: JsonQuestion): List<Answer> {
        if (question.flags.contains(QuestionFlag.RANDOM_BO_PUNCT)) {
            if (question.answers.size != 1) {
                throw RuntimeException("Expected only 1 answer if RANDOM_BO_PUNCT is set, but was: ${question.answers.size}!")
            }
            val answer = question.answers[0]
            val list = mutableListOf(Answer(answer.text, isCorrect = true))
            // TODO this has to be done on level higher up, each time we display the question
            list.addAll(generateRandomAnswers(PunctCoordinate.parse(answer.text)))
            return list
        }
        return question.answers.mapIndexed { i, answer ->
            Answer(answer.text, isCorrect = i == 0)
        }
    }

    private fun generateRandomAnswers(except: PunctCoordinate): List<Answer> {
        return 1.rangeTo(3).map {
            Answer(BoPunctRandomGenerator.generate(except).toNiceString())
        }
    }
}

enum class QuestionFlag {
    RANDOM_BO_PUNCT
}

data class JsonAnswer(val text: String)
data class JsonQuestion(
        val text: String,
        val flags: List<QuestionFlag>,
        val answers: List<JsonAnswer>)

data class JsonCatalog(val questions: List<JsonQuestion>)

class JsonQuestionReader {

    companion object {
        private val LOG = yobu.christophpickl.github.com.yobu.misc.LOG(JsonQuestionReader::class.java)
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
        return JsonQuestion(
                json.getString("text"),
                json.optJSONArray("flags")?.let { toFlags(it) } ?: emptyList(),
                json.getJSONArray("answers").map { toJsonAnswer(it) }
        )
    }

    private fun toFlags(json: JSONArray): List<QuestionFlag> {
        return json.mapStrings { QuestionFlag.valueOf(it) }
    }

    private fun toJsonAnswer(json: JSONObject): JsonAnswer {
        return JsonAnswer(json.getString("text"))
    }
}

fun <T> JSONArray.mapStrings(transform: (String) -> T): List<T> {
    return 1.rangeTo(this.length()).map {
        transform(this.getString(it - 1))
    }
}

fun <T> JSONArray.map(transform: (JSONObject) -> T): List<T> {
    return 1.rangeTo(this.length()).map {
        transform(getJSONObject(it - 1))
    }
}