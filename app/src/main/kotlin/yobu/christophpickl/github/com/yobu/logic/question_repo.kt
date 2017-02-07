package yobu.christophpickl.github.com.yobu.logic

import yobu.christophpickl.github.com.yobu.Question
import java.util.*

class QuestionRepo(
        questions: List<Question>,
        private val statisticService: QuestionStatisticService
) {

    private val questionsById = questions.associateBy { it.id }

    fun nextQuestion(): Question {
        val question = statisticService.nextQuestion(questionsById)
        return question.copy(answers = question.answers.randomizeElements())
    }

}
