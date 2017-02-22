package yobu.christophpickl.github.com.yobu.logic

import yobu.christophpickl.github.com.yobu.Question
import yobu.christophpickl.github.com.yobu.common.YobuException

interface QuestionSelector {
    fun nextQuestion(): Question
    fun questionById(id: String): Question
}

class QuestionSelectorImpl(
        questionLoader: QuestionLoader,
        private val statisticService: StatisticService
): QuestionSelector {

    private val questionsById = questionLoader.load().associateBy { it.id }

    override fun nextQuestion(): Question {
        val question = statisticService.nextQuestion(questionsById)
        return question.copy(answers = question.answers.randomizeElements())
    }

    override fun questionById(id: String) = questionsById[id] ?: throw YobuException("")

}
