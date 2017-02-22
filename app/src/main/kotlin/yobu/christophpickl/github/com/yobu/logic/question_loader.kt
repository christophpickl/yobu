package yobu.christophpickl.github.com.yobu.logic

import yobu.christophpickl.github.com.yobu.Question

interface QuestionLoader {
    fun load(): List<Question>
}

class QuestionLoaderImpl(
        private val questionsGenerator: QuestionsGenerator
) : QuestionLoader {

    private val questions by lazy { StaticQuestions.questions.plus(questionsGenerator.generateDefaultQuestions()) }

    override fun load() = questions

}
