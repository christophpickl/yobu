package yobu.christophpickl.github.com.yobu.logic

import yobu.christophpickl.github.com.yobu.Question
import java.util.*

class QuestionRepo(private val questions: List<Question>) {

    private var index = 0

    fun nextQuestion(): Question {
        val question = questions[index]
        index++
        if (index >= questions.size) {
            index = 0
        }
        return question.copy(answers = question.answers.randomizeElements())
    }

}

fun <T> List<T>.randomizeElements(): List<T> {
    return toMutableList().apply {
        Collections.shuffle(this)
    }
}
