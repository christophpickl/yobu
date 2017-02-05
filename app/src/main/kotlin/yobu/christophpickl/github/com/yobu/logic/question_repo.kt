package yobu.christophpickl.github.com.yobu.logic

import yobu.christophpickl.github.com.yobu.Question
import java.util.*

class QuestionRepo(private val questions: List<Question>) {

    fun nextQuestion(): Question {
        // TODO implement statistics! not simply random...
        val question = questions.randomElement()
        return question.copy(answers = question.answers.randomizeElements())
    }

}
