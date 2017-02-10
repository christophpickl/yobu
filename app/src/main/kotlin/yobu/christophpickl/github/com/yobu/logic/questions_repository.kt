package yobu.christophpickl.github.com.yobu.logic

import org.json.JSONArray
import org.json.JSONObject
import yobu.christophpickl.github.com.yobu.*
import yobu.christophpickl.github.com.yobu.common.readString
import java.io.InputStream


class QuestionsLoader {

    private val boGenerator = BoPunctGenerator()


    // TODO mehr fragen
//    * wer liegt nicht auf KG? => Lu, Di, Mi, Ni, Gb, Le
//    * wer liegt zwischen brustwarzen? => Pk (KG17)
//    * wer liegt (als einziger) (2C) seitlich vom nabel? => Di (Ma25)
    fun load(): List<Question> {
        return listOf(
                Question( // wer auf der 11./12. rippe? => Mi (Le13), Ni (Gb25)
                        id = "BoRippen",
                        text = "Welche zwei Bo Punkte liegen auf den Rippenenden?",
                        answers = listOf(
                                Answer("MP, Ni", isRight = true),
                                Answer("MP, KG"),
                                Answer("Gb, Le"),
                                Answer("Ni, Bl")
                        )
                ),
                Question( // wer in 6./7. ICR? => Le (Le14, 6.), Gb (Gb24, 7.)
                        id = "BoICR",
                        text = "Welche zwei Bo Punkte liegen in den ICRs?",
                        answers = listOf(
                                Answer("Le, Gb", isRight = true),
                                Answer("MP, Ni"),
                                Answer("Di, Le"),
                                Answer("Mi, Gb")
                        )
                ),
                Question( // wer liegt auf seinem eigenen meridian? => Lu(1), Gb(24), Le(14)
                        id = "BoOwnMerid",
                        text = "Welcher Bo Punkt liegt auf seinem eigenen Meridian?",
                        answers = listOf(
                                // or maybe reduce to one single right answer containing "Lu, Gb, Le"?
                                Answer("Lu", isRight = true),
                                Answer("Gb", isRight = true),
                                Answer("Le", isRight = true),
                                Answer("MP"),
                                Answer("Ma"),
                                Answer("Bl"),
                                Answer("Ni")
                        )
                )
        )
//        return JsonQuestionReader().read(questionsStream).questions.map { question ->
//            Question(question.id, question.text, transformAnswers(question))
//        }
//                .plus(generateDefaultQuestions())
//        return generateDefaultQuestions()
    }


    private fun transformAnswers(question: JsonQuestion): List<Answer> {
//        if (question.flags.contains(QuestionFlag.RANDOM_BO_PUNCT)) {
//            if (question.answers.size != 1) {
//                throw RuntimeException("Expected only 1 answer if RANDOM_BO_PUNCT is set, but was: ${question.answers.size}!")
//            }
//            val answer = question.answers[0]
//            val list = mutableListOf(Answer(answer.text, isRight = true))
//            // TO DO this has to be done on level higher up, each time we display the question
//            list.addAll(boGenerator.generateRandomAnswers(PunctCoordinate.parse(answer.text)))
//            return list
//        }
        return question.answers.mapIndexed { i, answer ->
            Answer(answer.text, isRight = i == 0)
        }
    }
}

enum class QuestionFlag {
//    RANDOM_BO_PUNCT
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