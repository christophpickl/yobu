package yobu.christophpickl.github.com.yobu.logic

import org.json.JSONArray
import org.json.JSONObject
import yobu.christophpickl.github.com.yobu.*
import yobu.christophpickl.github.com.yobu.common.readString
import java.io.InputStream


private enum class BoDistributionItem(val percent: Int, val meridian: Meridian) {
    KG(60, Meridian.KG),
    Lu(10, Meridian.Lu),
    Ma(10, Meridian.Ma),
    Le(10, Meridian.Le),
    Gb(10, Meridian.Gb)
    // TODO special type: use same meridian as "except" instance but different point
}
class BoPunctGenerator(private val randX: RandX = RandXImpl) {

    private val distribution = Distribution(BoDistributionItem.values().map { DistributionItem(it.percent, it.meridian) })

    fun generate(except: PunctCoordinate): PunctCoordinate {
        val randMeridian = randX.distributed(distribution)

        val exceptPoint = if(randMeridian == except.meridian) except.point else null
        val randPoint = randX.randomBetween(1, randMeridian.points, exceptPoint)

        return PunctCoordinate(randMeridian, randPoint)
    }

    fun generateAnswers(count: Int, except: PunctCoordinate): List<Answer> {
        // TODO check count limits
        val result = mutableSetOf<Answer>()
        while (result.size != count) {
            // using a Set will ensure there are no duplicates
            result += Answer(generate(except).label)
        }
        return result.toList()
    }
}

class CatalogsRepository {

    private val boGenerator = BoPunctGenerator()

    fun load(questionsStream: InputStream): List<Question> {
//        return JsonQuestionReader().read(questionsStream).questions.map { question ->
//            Question(question.id, question.ext, transformAnswers(question))
//        }
//                .plus(generateDefaultQuestions())
        return generateDefaultQuestions()
    }

    private fun generateDefaultQuestions(): List<Question> {
        return BoRelevantMeridian.values().map { boMeridian ->
            Question(
                    id = "BoPunkt${boMeridian.labelShort}",
                    text = "Bo Punkt von ${boMeridian.labelLong}?",
                    answers = listOf(Answer(boMeridian.boPunct.label, isRight = true))
                            .plus(generateRandomAnswers(boMeridian.boPunct))
            )
        }
    }

    private fun transformAnswers(question: JsonQuestion): List<Answer> {
        if (question.flags.contains(QuestionFlag.RANDOM_BO_PUNCT)) {
            if (question.answers.size != 1) {
                throw RuntimeException("Expected only 1 answer if RANDOM_BO_PUNCT is set, but was: ${question.answers.size}!")
            }
            val answer = question.answers[0]
            val list = mutableListOf(Answer(answer.text, isRight = true))
            // TODO this has to be done on level higher up, each time we display the question
            list.addAll(generateRandomAnswers(PunctCoordinate.parse(answer.text)))
            return list
        }
        return question.answers.mapIndexed { i, answer ->
            Answer(answer.text, isRight = i == 0)
        }
    }

    private fun generateRandomAnswers(except: PunctCoordinate): List<Answer> {
        return boGenerator.generateAnswers(3, except)
    }
}

enum class QuestionFlag {
    RANDOM_BO_PUNCT
}

data class JsonAnswer(val text: String)
data class JsonQuestion(
        val id: String,
        val text: String,
        val flags: List<QuestionFlag>,
        val answers: List<JsonAnswer>)

data class JsonCatalog(val questions: List<JsonQuestion>)

class JsonQuestionReader {

    companion object {
        private val LOG = yobu.christophpickl.github.com.yobu.common.LOG(JsonQuestionReader::class.java)
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
                json.getString("id"),
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