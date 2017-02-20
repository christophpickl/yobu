package yobu.christophpickl.github.com.yobu.logic

import yobu.christophpickl.github.com.yobu.DISABLE_RANDOM_QUESTIONS
import yobu.christophpickl.github.com.yobu.Question
import java.util.*

class QuestionSelector(
        questions: List<Question>,
        private val statisticService: QuestionStatisticService
) {

    private val sequentialSelector = SequentialQuestionSelector(questions)
    private val questionsById = questions.associateBy { it.id }

    fun nextQuestion(): Question {
        val question = if (DISABLE_RANDOM_QUESTIONS) {
            sequentialSelector.nextQuestion()
        } else {
            statisticService.nextQuestion(questionsById)
        }
        return question.copy(answers = question.answers.randomizeElements())
    }

    fun questionById(id: String) = questionsById[id]!!

}

private class SequentialQuestionSelector(private val questions: List<Question>) {
    private var currentIndex = 0
    fun nextQuestion(): Question {
        val question = questions[currentIndex]
        if (currentIndex == questions.size - 1) {
            currentIndex = 0
        } else {
            currentIndex++
        }
        return question
    }

}
