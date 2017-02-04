package yobu.christophpickl.github.com.yobu

data class Answer(val text: String, val isCorrect: Boolean = false)

data class Question(
        val text: String,
        val answers: List<Answer>
        )

class QuestionRepo {

    private var index = 0

    private val questions = listOf(
            Question("Gut fuer Grippevorbegugng?", listOf(Answer("Di4", true), Answer("He1"), Answer("Lu7"))),
            Question("Bo punkt fuer He?", listOf(Answer("Gb1", true), Answer("KG13")))
    )

    fun nextQuestion(): Question {
        val question = questions[index]
        index++
        if (index >= questions.size) {
            index = 0
        }
        return question
    }

}
