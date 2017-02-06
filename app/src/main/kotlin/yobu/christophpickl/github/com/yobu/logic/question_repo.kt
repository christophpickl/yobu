package yobu.christophpickl.github.com.yobu.logic

import yobu.christophpickl.github.com.yobu.Question
import java.util.*

class QuestionRepo(
        private val questions: List<Question>,
        private val statisticService: QuestionStatisticService
) {

    private val questionsById = questions.associateBy { it.id }

    fun nextQuestion(): Question {
        val questionId = statisticService.nextQuestionId()
        val question = if(questionId == null) questions.randomElement()
                else questionsById[questionId] ?: throw IllegalArgumentException("Question with ID '$questionId' not existing!")

        return question.copy(answers = question.answers.randomizeElements())
    }

}
