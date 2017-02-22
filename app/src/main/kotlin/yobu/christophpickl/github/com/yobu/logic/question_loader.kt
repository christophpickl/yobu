package yobu.christophpickl.github.com.yobu.logic

import yobu.christophpickl.github.com.yobu.Question
import yobu.christophpickl.github.com.yobu.common.YobuException

interface QuestionLoader {
    val questions: List<Question>
    val questionsById: Map<String, Question>

    fun questionById(id: String): Question
}

class QuestionLoaderImpl(
        private val questionsGenerator: QuestionsGenerator
) : QuestionLoader {

    override val questions by lazy { StaticQuestions.questions.plus(questionsGenerator.generateDefaultQuestions()) }
    override val questionsById by lazy { questions.associateBy { it.id } }

    override fun questionById(id: String) = questionsById[id] ?: throw YobuException("Question not found by ID: '$id'!")

}
