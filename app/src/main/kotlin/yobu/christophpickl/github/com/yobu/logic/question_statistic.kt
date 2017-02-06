package yobu.christophpickl.github.com.yobu.logic

import android.content.Context
import yobu.christophpickl.github.com.yobu.Question
import yobu.christophpickl.github.com.yobu.logic.persistence.QuestionStatisticsSqliteRepository

class QuestionStatisticService(
        private val repository: QuestionStatisticsRepository
) {
    constructor(context: Context) : this(CachedQuestionStatisticsRepository(context, QuestionStatisticsSqliteRepository(context)))

    fun correctAnswered(question: Question) {
        answered(question, correct = true)
    }

    fun wrongAnswered(question: Question) {
        answered(question, correct = false)
    }

    private fun answered(question: Question, correct: Boolean) {
        val maybeStat = repository.read(question.id)

        repository.insertOrUpdate(QuestionStatistic(
                id = question.id,
                countCorrect = if (correct) {
                    maybeStat?.countCorrect?.inc() ?: 1
                } else {
                    maybeStat?.countCorrect ?: 0
                }
        ))
    }

    fun nextQuestionId(): String? {
        // FIXME compute next question based on some algorithm
        return null
    }
}

/**
 * Model.
 */
data class QuestionStatistic(
        val id: String,
        val countCorrect: Int
) {
    companion object // needed for (test) extensions
}


/**
 * Repository interface.
 */
interface QuestionStatisticsRepository {
    fun insertOrUpdate(statistic: QuestionStatistic)
    fun readAll(): List<QuestionStatistic>
    fun read(id: String): QuestionStatistic?
}

/**
 * Avoid readAll communication to sqlite by caching.
 */
class CachedQuestionStatisticsRepository(
        context: Context,
        private val sqliteDelegate: QuestionStatisticsRepository = QuestionStatisticsSqliteRepository(context)
) : QuestionStatisticsRepository {

    private val cache = mutableMapOf<String, QuestionStatistic>()
    private var firstReadAll = true

    override fun read(id: String): QuestionStatistic? = cache[id]

    override fun readAll(): List<QuestionStatistic> {
        if (firstReadAll) {
            cache.putAll(sqliteDelegate.readAll().associateBy { it.id })
            firstReadAll = false
        }
        return cache.values.toList()
    }

    override fun insertOrUpdate(statistic: QuestionStatistic) {
        cache.put(statistic.id, statistic)
        sqliteDelegate.insertOrUpdate(statistic)
    }

}