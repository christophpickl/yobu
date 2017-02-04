package yobu.christophpickl.github.com.yobu

data class Answer(val text: String, val isCorrect: Boolean = false)

data class Question(
        val text: String,
        val answers: List<Answer>
        )

class QuestionRepo {

    private var index = 0

    private val questions = listOf(
            Question("Is it good?", listOf(Answer("Yes", true), Answer("No"))),
            Question("Are you happy?", listOf(Answer("Yes", true), Answer("No")))
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
