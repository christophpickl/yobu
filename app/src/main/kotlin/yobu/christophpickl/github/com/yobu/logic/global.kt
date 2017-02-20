package yobu.christophpickl.github.com.yobu.logic

import android.content.Context
import yobu.christophpickl.github.com.yobu.Question
import yobu.christophpickl.github.com.yobu.logic.persistence.QuestionStatisticsSqliteRepository

// ======================== shared among activities ========================

object GlobalDb {

    private var repo: QuestionStatisticsRepository? = null

    fun getRepo(context: Context): QuestionStatisticsRepository {
        if (repo == null) {
            repo = CachedQuestionStatisticsRepository(QuestionStatisticsSqliteRepository(context))
        }
        return repo!!
    }
}

object GlobalQuestions {

    private val questionsGenerator = QuestionsGenerator()

    val allQuestions: List<Question> by lazy {
        QuestionsLoader().load()
                .plus(questionsGenerator.generateDefaultQuestions())
    }

}
